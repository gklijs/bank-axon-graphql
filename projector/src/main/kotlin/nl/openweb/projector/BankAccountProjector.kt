package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.error.BankExceptionStatusCode
import nl.openweb.api.bank.error.BankQueryException
import nl.openweb.api.bank.event.BankAccountCreatedEvent
import nl.openweb.api.bank.event.UserAddedEvent
import nl.openweb.api.bank.event.UserRemovedEvent
import nl.openweb.api.bank.query.BankAccount
import nl.openweb.api.bank.query.BankAccountList
import nl.openweb.api.bank.query.FindBankAccountQuery
import nl.openweb.api.bank.query.FindBankAccountsForUserQuery
import nl.openweb.api.user.error.UserExceptionStatusCode
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.interceptors.ExceptionHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("bank-accounts")
@RequiredArgsConstructor
class BankAccountProjector(
    val bankAccountRepository: BankAccountRepository,
) {
    @EventHandler
    fun on(event: BankAccountCreatedEvent) {
        val newAccount = BankAccountSummary(
            event.iban,
            event.token,
            0,
            mutableListOf(event.username),
            mutableListOf()
        )
        bankAccountRepository.save(newAccount)
    }

    @EventHandler
    fun on(event: UserAddedEvent) {
        val bankAccount = bankAccountRepository.findById(event.iban)
            .orElseThrow { IllegalArgumentException("bank account with {${event.iban}} could not be found") }
        bankAccount.users.add(event.username)
        bankAccountRepository.save(bankAccount);
    }

    @EventHandler
    fun on(event: UserRemovedEvent) {
        val bankAccount = bankAccountRepository.findById(event.iban)
            .orElseThrow { IllegalArgumentException("bank account with {${event.iban}} could not be found") }
        bankAccount.users.remove(event.username)
        bankAccountRepository.save(bankAccount);
    }

    @QueryHandler
    fun handle(query: FindBankAccountQuery): BankAccount {
        return bankAccountRepository.findById(query.iban)
            .map { b ->
                BankAccount(
                    b.iban,
                    b.token,
                    b.balance,
                )
            }
            .orElseThrow { IllegalArgumentException("bank account with {${query.iban}} could not be found") }
    }

    @QueryHandler
    fun handle(query: FindBankAccountsForUserQuery): BankAccountList {
        val bankAccountList = BankAccountList();
        bankAccountRepository.findAllByUsers(query.username)
            .forEach { b ->
                bankAccountList.add(
                    BankAccount(b.iban, b.token, b.balance)
                )
            }
        return bankAccountList;
    }

    @ExceptionHandler(resultType = IllegalArgumentException::class)
    fun handle(exception: IllegalArgumentException) {
        val statusCode = if (exception.message!!.endsWith("could not be found")) {
            BankExceptionStatusCode.BANK_ACCOUNT_NOT_FOUND
        } else {
            UserExceptionStatusCode.UNKNOWN_EXCEPTION
        }
        throw BankQueryException(exception.message!!, exception, statusCode)
    }
}