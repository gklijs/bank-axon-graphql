package nl.openweb.query

import nl.openweb.api.account.AccountCreationSucceedEvent
import nl.openweb.logging.LoggerDelegate
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component


@Component
class AccountSummaryProjection(
        //private val entityManager: EntityManager,
        //private val queryUpdateEmitter: QueryUpdateEmitter
        ) {

    private val logger by LoggerDelegate()

    @EventHandler
    fun on(accountCreationSucceedEvent: AccountCreationSucceedEvent) {
/*        entityManager.persist(AccountSummary(accountCreationSucceedEvent.id,
                accountCreationSucceedEvent.username,
                accountCreationSucceedEvent.iban,
                accountCreationSucceedEvent.token,
                0))
        val query = entityManager.createQuery("SELECT COUNT(*)", Long::class.java)
        val total = query.singleResult*/
        logger.info("Received")
    }

}