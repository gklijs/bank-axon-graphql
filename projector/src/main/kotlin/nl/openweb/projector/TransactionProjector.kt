package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.event.MoneyCreditedEvent
import nl.openweb.api.bank.event.MoneyDebitedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("transactions")
@RequiredArgsConstructor
class TransactionProjector(
    val transactionRepository: TransactionRepository,
    val transferRepository: TransferRepository,
    val bankAccountRepository: BankAccountRepository,
) {
    @EventHandler
    fun on(event: MoneyDebitedEvent) {
        val transfer = transferRepository.findById(event.transactionId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance - transfer.amount
        var transaction = TransactionSummary(
            0,
            transfer.tranferFrom,
            transfer.transferTo,
            0 - transfer.amount,
            newBalance,
            transfer.description,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transactions.add(transaction)
        bankAccountRepository.save(account)
    }

    @EventHandler
    fun on(event: MoneyCreditedEvent) {
        val transfer = transferRepository.findById(event.transactionId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance + transfer.amount
        var transaction = TransactionSummary(
            0,
            transfer.transferTo,
            transfer.tranferFrom,
            transfer.amount,
            newBalance,
            transfer.description,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transactions.add(transaction)
        bankAccountRepository.save(account)
    }
}