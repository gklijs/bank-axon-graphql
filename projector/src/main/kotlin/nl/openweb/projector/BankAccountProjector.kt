package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.event.BankAccountCreatedEvent
import nl.openweb.api.bank.query.BankAccount
import nl.openweb.api.bank.query.FindBankAccountQuery
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
class BankAccountProjector(
    val bankAccountRepository: BankAccountRepository
) {
    @EventHandler
    fun on(event: BankAccountCreatedEvent) {
        val newAccount = BankAccountSummary(
            event.iban,
            event.token,
            0,
            mutableListOf()
        )
        bankAccountRepository.save(newAccount)
    }

    @QueryHandler
    fun handle(query: FindBankAccountQuery): BankAccount? {
        return bankAccountRepository.findById(query.iban)
            .map { b ->
                BankAccount(
                    b.iban,
                    b.token,
                    b.balance,
                )
            }
            .orElse(null)
    }
}