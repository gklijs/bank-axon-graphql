package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.query.BankAccount
import nl.openweb.api.user.event.BankAccountAddedEvent
import nl.openweb.api.user.event.BankAccountRemovedEvent
import nl.openweb.api.user.event.UserAccountCreatedEvent
import nl.openweb.api.user.query.FindUserAccountQuery
import nl.openweb.api.user.query.UserAccount
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class UserAccountProjector(
    val userAccountRepository: UserAccountRepository,
    val bankAccountRepository: BankAccountRepository
) {
    @EventHandler
    fun on(event: UserAccountCreatedEvent) {
        val newAccount = UserAccountSummary(
            event.username,
            event.username,
            mutableListOf()
        )
        userAccountRepository.save(newAccount)
    }

    @EventHandler
    fun on(event: BankAccountAddedEvent) {
        val userAccount = userAccountRepository.findById(event.username).orElseThrow()
        val bankAccount = bankAccountRepository.findById(event.iban).orElseThrow()
        userAccount.bankAccounts.add(bankAccount)
        userAccountRepository.save(userAccount);
    }

    @EventHandler
    fun on(event: BankAccountRemovedEvent) {
        val userAccount = userAccountRepository.findById(event.username).orElseThrow()
        val bankAccount = bankAccountRepository.findById(event.iban).orElseThrow()
        userAccount.bankAccounts.remove(bankAccount)
        userAccountRepository.save(userAccount);
    }

    @QueryHandler
    fun handle(query: FindUserAccountQuery): UserAccount {
        return userAccountRepository.findById(query.username)
            .map { u -> UserAccount(
                u.username,
                u.password,
                u.bankAccounts.map { b -> BankAccount(
                    b.iban,
                    b.token,
                    b.balance
                )}
            ) }
            .orElseThrow()
    }
}