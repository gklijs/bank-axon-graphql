package tech.gklijs.projector

import lombok.RequiredArgsConstructor
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.interceptors.ExceptionHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import tech.gklijs.api.bank.error.BankExceptionStatusCode
import tech.gklijs.api.user.error.UserExceptionStatusCode
import tech.gklijs.api.user.error.UserQueryException
import tech.gklijs.api.user.event.UserAccountCreatedEvent
import tech.gklijs.api.user.query.FindUserAccountQuery
import tech.gklijs.api.user.query.UserAccount

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
        throw UserQueryException(
            exception.message!!,
            exception,
            statusCode
        )
    }
}