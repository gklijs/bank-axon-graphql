package nl.openweb.commandhandler

import nl.openweb.api.account.AccountSummary
import nl.openweb.commandhandler.Utils.invalidFrom
import nl.openweb.commandhandler.Utils.isValidOpenIban
import nl.openweb.data.BalanceChanged
import nl.openweb.data.ConfirmMoneyTransfer
import nl.openweb.data.MoneyTransferConfirmed
import nl.openweb.data.MoneyTransferFailed
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class MoneyTransferProcessor() {
    fun getResponses(k: String, v: ConfirmMoneyTransfer): List<KeyValue<String, SpecificRecord>> {
        return handleMoneyTransfer(k, v)
    }

    private fun cmtHandled(k: String, v: ConfirmMoneyTransfer, cmt: Cmt): List<KeyValue<String, SpecificRecord>> {
        val result: SpecificRecord = if (cmt.reason.isBlank()) {
            MoneyTransferConfirmed(v.id)
        } else {
            MoneyTransferFailed(v.id, cmt.reason)
        }
        return listOf(KeyValue(k, result))
    }

    private fun handleMoneyTransfer(k: String, v: ConfirmMoneyTransfer): List<KeyValue<String, SpecificRecord>> {
        val reason: String
        lateinit var transfer: Result
        when {
            v.from.invalidFrom() -> reason = "from is invalid"
            v.from == v.to -> reason = "from and to can't be same for transfer"
            else -> {
                transfer = tryTransfer(k, v)
                reason = transfer.reason
            }
        }
        //cmtRepository.save(Cmt(UUID.nameUUIDFromBytes(v.id.bytes()), reason, LocalDateTime.now()))
        return if (reason.isBlank()) {
            transfer.responses
        } else {
            listOf(KeyValue(k, MoneyTransferFailed(v.id, reason) as SpecificRecord))
        }
    }

    private fun tryTransfer(k: String, v: ConfirmMoneyTransfer): Result {
        val responses = mutableListOf<KeyValue<String, SpecificRecord>>()
        val fromList : List<AccountSummary> = Collections.emptyList()
        val now = LocalDateTime.now()
/*        if (fromList.isNotEmpty()) {
            val from = fromList.first()
            when {
                v.token != from.token -> return Result("invalid token", responses)
                from.balance - v.amount < from.lmt -> return Result("insufficient funds", responses)
                else -> {
                    val newFrom = Balance(from.balanceId, k, from.iban, from.token, from.amount - v.amount, from.lmt, from.createdAt, now)
                    balanceRepository.save(newFrom)
                    responses.add(KeyValue(from.iban, BalanceChanged(from.iban, newFrom.amount, -v.amount, v.to, v.description)))
                }
            }
        }*/
        val toList : List<AccountSummary> = Collections.emptyList()
        if (toList.isNotEmpty()) {
            val to = toList.first()
            //val newTo = Balance(to.balanceId, k, to.iban, to.token, to.amount + v.amount, to.lmt, to.createdAt, now)
            //balanceRepository.save(newTo)
            responses.add(KeyValue(to.iban, BalanceChanged(to.iban, 50, v.amount, v.from, v.description)))
        }
        responses.add(KeyValue(k, MoneyTransferConfirmed(v.id)))
        return Result("", responses)
    }

    data class Result(val reason: String, val responses: List<KeyValue<String, SpecificRecord>>)
}