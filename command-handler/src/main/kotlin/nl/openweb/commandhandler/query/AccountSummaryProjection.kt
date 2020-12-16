package nl.openweb.commandhandler.query

import nl.openweb.api.bank.event.BankAccountCreatedEvent
import nl.openweb.commandhandler.AccountSummary
import nl.openweb.logging.LoggerDelegate
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class AccountSummaryProjection(
        private val entityManager: EntityManager,
        private val queryUpdateEmitter: QueryUpdateEmitter
) {

    private val logger by LoggerDelegate()

    @EventHandler
    fun on(accountCreationSucceedEvent: BankAccountCreatedEvent) {
        val summary = AccountSummary(
                accountCreationSucceedEvent.id,
                accountCreationSucceedEvent.username,
                accountCreationSucceedEvent.iban,
                accountCreationSucceedEvent.token,
                0
        )
        entityManager.persist(summary)
        val query = entityManager.createNamedQuery("AccountSummary.total")
        val total = query.singleResult
        logger.info("Received {}", total)
    }

}