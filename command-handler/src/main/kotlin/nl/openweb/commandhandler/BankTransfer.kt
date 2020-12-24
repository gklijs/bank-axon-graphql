package nl.openweb.commandhandler

import nl.openweb.api.bank.command.MarkTransferCompletedCommand
import nl.openweb.api.bank.command.MarkTransferFailedCommand
import nl.openweb.api.bank.command.MoneyTransferCommand
import nl.openweb.api.bank.event.TransferCompletedEvent
import nl.openweb.api.bank.event.TransferFailedEvent
import nl.openweb.api.bank.event.TransferStartedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate


@Aggregate
class BankTransfer {

    @AggregateIdentifier
    private var transferId: String? = null
    private var status: Status = Status.STARTED

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    protected fun handle(cmd: MoneyTransferCommand) {
        AggregateLifecycle.apply(
            TransferStartedEvent(
                cmd.transferId,
                cmd.token,
                cmd.amount,
                cmd.from,
                cmd.to,
                cmd.description,
                cmd.username
            )
        )
    }

    @CommandHandler
    fun handle(command: MarkTransferCompletedCommand) {
        AggregateLifecycle.apply(TransferCompletedEvent(command.transferId))
    }

    @CommandHandler
    fun handle(command: MarkTransferFailedCommand) {
        AggregateLifecycle.apply(TransferFailedEvent(command.transferId, command.reason))
    }

    @EventHandler
    fun on(event: TransferStartedEvent) {
        this.transferId = event.transferId
    }

    @EventHandler
    fun on(event: TransferCompletedEvent) {
        status = Status.COMPLETED
    }

    @EventHandler
    fun on(event: TransferFailedEvent) {
        status = Status.FAILED
    }

    private enum class Status {
        STARTED, FAILED, COMPLETED
    }
}