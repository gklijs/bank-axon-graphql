package tech.gklijs.projector

import lombok.RequiredArgsConstructor
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component
import tech.gklijs.api.bank.event.MoneyCreditedEvent
import tech.gklijs.api.bank.event.MoneyDebitedEvent
import tech.gklijs.api.bank.event.MoneyReturnedEvent
import tech.gklijs.api.bank.query.AllLastTransactionsQuery
import tech.gklijs.api.bank.query.LastTransactionQuery
import tech.gklijs.api.bank.query.Transaction
import tech.gklijs.api.bank.query.TransactionList

@Component
@ProcessingGroup("transactions")
@RequiredArgsConstructor
class TransactionProjector(
    val transactionRepository: TransactionRepository,
    val transferRepository: TransferRepository,
    val bankAccountRepository: BankAccountRepository,
    val queryUpdateEmitter: QueryUpdateEmitter,
) {
    @EventHandler
    fun on(event: MoneyDebitedEvent) {
        val transfer = transferRepository.findById(event.transferId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance - transfer.amount
        var transaction = TransactionSummary(
            0,
            transfer.tranferFrom,
            transfer.transferTo,
            0 - transfer.amount,
            newBalance,
            transfer.description,
            transfer.transferId,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transactions.add(transaction)
        bankAccountRepository.save(account)
        queryUpdateEmitter.emit(LastTransactionQuery::class.java, { true }, transaction.asApi())
    }

    @EventHandler
    fun on(event: MoneyCreditedEvent) {
        val transfer = transferRepository.findById(event.transferId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance + transfer.amount
        var transaction = TransactionSummary(
            0,
            transfer.transferTo,
            transfer.tranferFrom,
            transfer.amount,
            newBalance,
            transfer.description,
            transfer.transferId,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transactions.add(transaction)
        bankAccountRepository.save(account)
        queryUpdateEmitter.emit(LastTransactionQuery::class.java, { true }, transaction.asApi())
    }

    @EventHandler
    fun on(event: MoneyReturnedEvent) {
        val transfer = transferRepository.findById(event.transferId).orElseThrow()
        val account = bankAccountRepository.findById(event.iban).orElseThrow()
        val newBalance = account.balance + transfer.amount
        val returnedTransaction = transactionRepository.findOneByTransferId(event.transferId)
            .map { t -> t.id.toString() }
            .orElse("unknown")
        var transaction = TransactionSummary(
            0,
            transfer.transferTo,
            transfer.tranferFrom,
            transfer.amount,
            newBalance,
            "Canceled transaction with id: $returnedTransaction because: ${event.reason}",
            transfer.transferId,
        )
        transaction = transactionRepository.save(transaction)
        account.balance = newBalance
        account.transactions.add(transaction)
        bankAccountRepository.save(account)
        queryUpdateEmitter.emit(LastTransactionQuery::class.java, { true }, transaction.asApi())
    }

    @QueryHandler
    fun handle(query: LastTransactionQuery): Transaction {
        return transactionRepository.findTopByOrderByIdDesc().asApi()
    }

    @QueryHandler
    fun handle(query: AllLastTransactionsQuery): TransactionList {
        val transactionList = TransactionList()
        transactionRepository.allLastTransactions()
            .asIterable()
            .forEach { t -> transactionList.add(t.asApi()) }
        return transactionList
    }
}