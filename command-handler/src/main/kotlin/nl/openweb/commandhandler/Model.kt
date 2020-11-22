package nl.openweb.commandhandler

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "balance")
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
@Table(name = "cac")
class Cac(
    @Id
    var uuid: UUID,
    var iban: String,
    var token: String,
    var reason: String,
    var createdAt: LocalDateTime
)

@Entity
@Table(name = "cmt")
class Cmt(
    @Id
    var uuid: UUID,
    var reason: String,
    var createdAt: LocalDateTime
)