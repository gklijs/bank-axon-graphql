package nl.openweb.projector

import org.springframework.data.jpa.repository.JpaRepository

interface UserAccountRepository : JpaRepository<UserAccountSummary, String>

interface BankAccountRepository : JpaRepository<BankAccountSummary, String> {
    fun findAllByUsers(username: String): List<BankAccountSummary>
}

interface TransactionRepository : JpaRepository<TransactionSummary, String>

interface TransferRepository : JpaRepository<TransferSummary, String>