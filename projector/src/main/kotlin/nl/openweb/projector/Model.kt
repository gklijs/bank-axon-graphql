package nl.openweb.projector

import javax.persistence.*

@Entity
class UserAccountSummary(
    @Id
    var username: String,
    var password: String,
    @ManyToMany
    var bankAccounts: MutableList<BankAccountSummary>,
)

@Entity
class BankAccountSummary(
    @Id
    var iban: String,
    var token: String,
    var balance: Long,
    @OneToMany
    var transaction: MutableList<TransactionSummary>,
)

@Entity
class TransactionSummary(
    @Id
    @GeneratedValue
    var id: Long,
    var iban: String,
    var fromTo: String,
    var changedBy: Long,
    var newBalance: Long,
    var descr: String,
)

@Entity
class TransferSummary(
    @Id
    var transactionId: String,
    var amount: Long,
    var from: String,
    var to: String,
    var description: String,
    var error: String? = null,
    var state: TransferState = TransferState.BEING_PROCESSED,
)

enum class TransferState {
    BEING_PROCESSED, FAILED, COMPLETED
}