package nl.openweb.commandhandler

import nl.openweb.api.bank.event.BankAccountCreatedEvent
import nl.openweb.api.bank.command.CreateBankAccountCommand
import nl.openweb.api.bank.event.MoneyCreditedEvent
import nl.openweb.api.bank.event.MoneyDebitedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class BankAccount {

    @AggregateIdentifier
    private var iban: String? = null
    private var token: String? = null
    private var balance = 0L
    private var limit = - 500

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    protected fun handle(cmd: CreateBankAccountCommand) {
        AggregateLifecycle.apply(
            BankAccountCreatedEvent(
                cmd.iban,
                Utils.getToken()
            )
        )
    }

    @EventSourcingHandler
    protected fun on(event: BankAccountCreatedEvent) {
        this.iban = event.iban
        this.token = event.token
    }

    @EventSourcingHandler
    protected fun on(event: MoneyDebitedEvent) {
        this.balance = balance - event.amount
    }

    @EventSourcingHandler
    protected fun on(event: MoneyCreditedEvent) {
        this.balance = balance + event.amount
    }
}