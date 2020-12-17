package nl.openweb.commandhandler

import nl.openweb.api.user.command.AddBankAccountCommand
import nl.openweb.api.user.command.CreateUserAccountCommand
import nl.openweb.api.user.command.RemoveBankAccountCommand
import nl.openweb.api.user.event.BankAccountAddedEvent
import nl.openweb.api.user.event.BankAccountRemovedEvent
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
    private var bankAccounts = mutableListOf<String>()

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

    @CommandHandler
    protected fun handle(cmd: AddBankAccountCommand) {
        //todo - check if token is valid
        AggregateLifecycle.apply(
            BankAccountAddedEvent(
                cmd.username,
                cmd.iban
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: RemoveBankAccountCommand) {
        if (! this.bankAccounts.contains(cmd.iban)) {
            throw IllegalArgumentException(
                "User did not have access to account with iban " + cmd.iban
            )
        }
        //todo - check if token is valid
        AggregateLifecycle.apply(
            BankAccountAddedEvent(
                cmd.username,
                cmd.iban
            )
        )
    }

    @EventSourcingHandler
    protected fun on(event: UserAccountCreatedEvent) {
        this.username = event.username
        this.password = event.password
    }

    @EventSourcingHandler
    protected fun on(event: BankAccountAddedEvent) {
        this.bankAccounts.add(event.iban)
    }

    @EventSourcingHandler
    protected fun on(event: BankAccountRemovedEvent) {
        this.bankAccounts.remove(event.iban)
    }
}