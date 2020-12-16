package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.event.*
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
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
            transfer.from,
            transfer.to,
            0 - transfer.amount,
            newBalance,
            transfer.description,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transaction.add(transaction)
        bankAccountRepository.save(account)
    }

    @EventHandler
    fun on(event: MoneyCreditedEvent) {
        val transfer = transferRepository.findById(event.transactionId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance + transfer.amount
        var transaction = TransactionSummary(
            0,
            transfer.to,
            transfer.from,
            transfer.amount,
            newBalance,
            transfer.description,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transaction.add(transaction)
        bankAccountRepository.save(account)
    }
}