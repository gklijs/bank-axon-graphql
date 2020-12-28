package nl.openweb.projector

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserAccountRepository : JpaRepository<UserAccountSummary, String>

interface BankAccountRepository : JpaRepository<BankAccountSummary, String> {
    fun findAllByUsers(username: String): List<BankAccountSummary>
}

interface TransactionRepository : JpaRepository<TransactionSummary, Long> {
    fun findTopByOrderByIdDesc(): TransactionSummary
    fun findOneByTransferId(transferId: String): Optional<TransactionSummary>

    @Query(
        value = "SELECT * FROM transaction_summary WHERE id IN (SELECT MAX(id) FROM transaction_summary GROUP BY iban) ORDER BY iban",
        nativeQuery = true
    )
    fun allLastTransactions(): List<TransactionSummary>
}

interface TransferRepository : JpaRepository<TransferSummary, String>