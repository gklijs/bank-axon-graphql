package nl.openweb.commandhandler

import nl.openweb.api.user.command.CreateUserAccountCommand
import nl.openweb.api.user.event.UserAccountCreatedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class UserAccount {

    @AggregateIdentifier
    private var username: String? = null
    private var password: String? = null

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    protected fun handle(cmd: CreateUserAccountCommand) {
        AggregateLifecycle.apply(
            UserAccountCreatedEvent(
                cmd.username,
                cmd.password
            )
        )
    }

    @EventSourcingHandler
    protected fun on(event: UserAccountCreatedEvent) {
        this.username = event.username
        this.password = event.password
    }
}