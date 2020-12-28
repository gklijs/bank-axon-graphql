package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.error.BankExceptionStatusCode
import nl.openweb.api.user.error.UserExceptionStatusCode
import nl.openweb.api.user.error.UserQueryException
import nl.openweb.api.user.event.UserAccountCreatedEvent
import nl.openweb.api.user.query.FindUserAccountQuery
import nl.openweb.api.user.query.UserAccount
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.interceptors.ExceptionHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("user-accounts")
@RequiredArgsConstructor
class UserAccountProjector(
    val userAccountRepository: UserAccountRepository,
) {
    @EventHandler
    fun on(event: UserAccountCreatedEvent) {
        val newAccount = UserAccountSummary(
            event.username,
            event.password
        )
        userAccountRepository.save(newAccount)
    }

    @QueryHandler
    fun handle(query: FindUserAccountQuery): UserAccount {
        return userAccountRepository.findById(query.username)
            .map { u ->
                UserAccount(
                    u.username,
                    u.password,
                )
            }
            .orElseThrow { IllegalArgumentException("user with ${query.username} could not be found") }
    }

    @ExceptionHandler(resultType = IllegalArgumentException::class)
    fun handle(exception: IllegalArgumentException) {
        val statusCode = if (exception.message!!.endsWith("could not be found")) {
            if (exception.message!!.startsWith("user")) {
                UserExceptionStatusCode.USER_ACCOUNT_NOT_FOUND
            } else {
                BankExceptionStatusCode.BANK_ACCOUNT_NOT_FOUND
            }
        } else {
            UserExceptionStatusCode.UNKNOWN_EXCEPTION
        }
        throw UserQueryException(exception.message!!, exception, statusCode)
    }
}