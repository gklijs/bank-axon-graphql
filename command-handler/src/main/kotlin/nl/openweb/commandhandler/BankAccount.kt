package nl.openweb.commandhandler

import nl.openweb.api.bank.command.AddUserCommand
import nl.openweb.api.bank.event.BankAccountCreatedEvent
import nl.openweb.api.bank.command.CreateBankAccountCommand
import nl.openweb.api.bank.command.RemoveUserCommand
import nl.openweb.api.bank.error.BankCommandException
import nl.openweb.api.bank.error.BankExceptionStatusCode
import nl.openweb.api.bank.event.MoneyCreditedEvent
import nl.openweb.api.bank.event.MoneyDebitedEvent
import nl.openweb.api.bank.event.UserAddedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.interceptors.ExceptionHandler
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
    private val users = mutableListOf<String>()

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    protected fun handle(cmd: CreateBankAccountCommand) {
        AggregateLifecycle.apply(
            BankAccountCreatedEvent(
                cmd.iban,
                Utils.getToken(),
                cmd.username
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: AddUserCommand) {
        if (! cmd.token.equals(this.token)){
            throw IllegalStateException("The supplied token is not valid, ${cmd.token} not equal to stored ${this.token}")
        }
        AggregateLifecycle.apply(
            UserAddedEvent(
                cmd.username,
                cmd.iban
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: RemoveUserCommand) {
        if (! cmd.token.equals(this.token)){
            throw IllegalStateException("The supplied token is not valid, ${cmd.token} not equal to stored ${this.token}")
        }
        if (! this.users.contains(cmd.username)) {
            throw IllegalStateException("The user ${cmd.username} is not an owner of the bank account")
        }
        if (this.users.size == 1 && this.balance != 0L) {
            throw IllegalStateException("User can't be removed because only owner and balance is not zero")
        }
        AggregateLifecycle.apply(
            UserAddedEvent(
                cmd.username,
                cmd.iban
            )
        )
    }

    @EventSourcingHandler
    protected fun on(event: BankAccountCreatedEvent) {
        this.iban = event.iban
        this.token = event.token
        this.users.add(event.username)
    }

    @EventSourcingHandler
    protected fun on(event: AddUserCommand) {
        this.users.add(event.username)
    }

    @EventSourcingHandler
    protected fun on(event: RemoveUserCommand) {
        this.users.remove(event.username)
    }

    @EventSourcingHandler
    protected fun on(event: MoneyDebitedEvent) {
        this.balance = balance - event.amount
    }

    @EventSourcingHandler
    protected fun on(event: MoneyCreditedEvent) {
        this.balance = balance + event.amount
    }

    @ExceptionHandler(resultType = IllegalStateException::class)
    fun handle(exception: IllegalStateException) {
        val statusCode = if (exception.message!!.contains("Insufficient")) {
            BankExceptionStatusCode.INSUFFICIENT_FUNDS
        } else if (exception.message!!.contains("The supplied token is not valid")) {
            BankExceptionStatusCode.INVALID_TOKEN
        } else if (exception.message!!.contains("is not an owner of the bank account")) {
            BankExceptionStatusCode.USER_IS_NO_OWNER
        } else if (exception.message!!.contains("balance is not zero")) {
            BankExceptionStatusCode.BALANCE_NOT_ZERO_SINGLE_OWNER
        } else {
            BankExceptionStatusCode.UNKNOWN_EXCEPTION
        }
        throw BankCommandException(exception.message, exception, statusCode)
    }
}