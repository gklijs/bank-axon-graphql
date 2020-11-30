package nl.openweb.commandhandler

import nl.openweb.api.account.AccountCreationSucceedEvent
import nl.openweb.api.account.CreateAccountCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Account {

    @AggregateIdentifier
    private var id: String? = null
    private var username: String? = null
    private var iban: String? = null
    private var token: String? = null
    private val balance = 0

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    protected fun handle(cmd: CreateAccountCommand) {
        apply(AccountCreationSucceedEvent(cmd.id, cmd.username, Utils.getIban(), Utils.getToken()))
    }

    @EventSourcingHandler
    protected fun on(event: AccountCreationSucceedEvent) {
        this.id = event.id
        this.username = event.username
        this.iban = event.iban
        this.token = event.token
    }
}