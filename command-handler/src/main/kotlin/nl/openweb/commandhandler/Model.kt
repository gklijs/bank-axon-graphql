package nl.openweb.commandhandler

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@NamedQueries(
        NamedQuery(name = "AccountSummary.total",
                query = "SELECT COUNT (*) FROM AccountSummary"))
class AccountSummary(
        @Id
        var id: String,
        var username: String,
        var iban: String,
        var token: String,
        var balance: Int,
)

@Entity
class Balance(
        @Id @GeneratedValue
        var balanceId: Int,
        var username: String,
        var iban: String,
        var token: String,
        var amount: Long,
        var lmt: Long,
        var createdAt: LocalDateTime,
        var updatedAt: LocalDateTime
)

@Entity
class Cac(
        @Id
        var uuid: UUID,
        var iban: String,
        var token: String,
        var reason: String,
        var createdAt: LocalDateTime
)

@Entity
class Cmt(
        @Id
        var uuid: UUID,
        var reason: String,
        var createdAt: LocalDateTime
)