package nl.openweb.commandhandler

import nl.openweb.commandhandler.Utils.toUuid
import nl.openweb.data.AccountCreationConfirmed
import nl.openweb.data.AccountCreationFailed
import nl.openweb.data.ConfirmAccountCreation
import org.apache.avro.specific.SpecificRecord
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class AccountCreationProcessor() {

    fun getResponse(cac: ConfirmAccountCreation): SpecificRecord {
        val cacId = cac.id.toUuid()
        return tryToGenerateAccount(cac, cacId)
    }

    private fun tryToGenerateAccount(cac: ConfirmAccountCreation, cacId: UUID): SpecificRecord {
        val iban = Utils.getIban()
        val balance = Collections.EMPTY_LIST //balanceRepository.findByIban(iban)
        val now = LocalDateTime.now()
        return if (balance.isEmpty()) {
            val token = Utils.getToken()
            //cacRepository.save(Cac(cacId, iban, token, "", now))
            //balanceRepository.save(Balance(0, cac.username, iban, token, 0L, -50000L, now, now))
            AccountCreationConfirmed(cac.id, iban, token)
        } else {
            val reason = "generated iban already exists, try again"
            //cacRepository.save(Cac(cacId, "", "", reason, now))
            AccountCreationFailed(cac.id, reason)
        }
    }
}
