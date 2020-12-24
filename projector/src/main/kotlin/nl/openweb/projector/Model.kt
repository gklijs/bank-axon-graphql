package nl.openweb.projector

import nl.openweb.api.bank.query.Transaction
import javax.persistence.*

@Entity
class UserAccountSummary(
    @Id
    var username: String,
    var password: String,
)

@Entity
class BankAccountSummary(
    @Id
    var iban: String,
    var token: String,
    var balance: Long,
    @ElementCollection
    var users: MutableList<String>,
    @OneToMany
    var transactions: MutableList<TransactionSummary>,
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
    var description: String,
    var transferId: String,
)

@Entity
class TransferSummary(
    @Id
    var transferId: String,
    var amount: Long,
    var tranferFrom: String,
    var transferTo: String,
    var description: String,
    var error: String? = null,
    var state: TransferSummaryState = TransferSummaryState.BEING_PROCESSED,
)

enum class TransferSummaryState {
    BEING_PROCESSED, FAILED, COMPLETED
}

fun TransactionSummary.asApi(): Transaction {
    return Transaction(
        this.id,
        this.iban,
        this.fromTo,
        this.changedBy,
        this.newBalance,
        this.description
    )
}