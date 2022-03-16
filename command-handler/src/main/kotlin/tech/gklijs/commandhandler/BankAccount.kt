package tech.gklijs.commandhandler

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.interceptors.ExceptionHandler
import org.axonframework.modelling.command.AggregateCreationPolicy
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate
import tech.gklijs.api.bank.command.*
import tech.gklijs.api.bank.error.BankCommandException
import tech.gklijs.api.bank.error.BankExceptionStatusCode
import tech.gklijs.api.bank.event.*
import tech.gklijs.api.bank.utils.TokenUtil

@Aggregate
class BankAccount {

    @AggregateIdentifier
    private var iban: String? = null
    private var token: String? = null
    private var balance = 0L
    private var limit = -50000L
    private val users = mutableListOf<String>()

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.ALWAYS)
    protected fun handle(cmd: CreateBankAccountCommand) {
        AggregateLifecycle.apply(
            BankAccountCreatedEvent(
                cmd.iban,
                TokenUtil.getToken(),
                cmd.username
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: AddUserCommand) {
        if (!cmd.token.equals(this.token)) {
            throw IllegalStateException("The supplied token is not valid, ${cmd.token} not equal to stored ${this.token}")
        }
        if (!this.users.contains(cmd.username)) {
            throw IllegalStateException("The user ${cmd.username} is already an owner of the bank account")
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
        if (!cmd.token.equals(this.token)) {
            throw IllegalStateException("The supplied token is not valid, ${cmd.token} not equal to stored ${this.token}")
        }
        if (!this.users.contains(cmd.username)) {
            throw IllegalStateException("The user ${cmd.username} is not an owner of this bank account")
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

    @CommandHandler
    protected fun handle(cmd: DebitMoneyCommand) {
        if (!cmd.token.equals(this.token)) {
            throw IllegalStateException("The supplied token is not valid, ${cmd.token} not equal to stored ${this.token}")
        }
        if (this.balance - cmd.amount < this.limit) {
            throw IllegalStateException("Insufficient funds, debiting the money would put the balance at ${this.balance - cmd.amount}")
        }
        if (!this.users.contains(cmd.username)) {
            throw IllegalStateException("The user ${cmd.username} is not an owner of this bank account")
        }
        AggregateLifecycle.apply(
            MoneyDebitedEvent(
                cmd.iban,
                cmd.amount,
                cmd.transferId,
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: CreditMoneyCommand) {
        AggregateLifecycle.apply(
            MoneyCreditedEvent(
                cmd.iban,
                cmd.amount,
                cmd.transferId,
            )
        )
    }

    @CommandHandler
    protected fun handle(cmd: ReturnMoneyCommand) {
        AggregateLifecycle.apply(
            MoneyReturnedEvent(
                cmd.iban,
                cmd.amount,
                cmd.transferId,
                cmd.reason,
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

    @EventSourcingHandler
    protected fun on(event: ReturnMoneyCommand) {
        this.balance = balance + event.amount
    }

    @ExceptionHandler(resultType = IllegalStateException::class)
    fun handle(exception: IllegalStateException) {
        val message = exception.message.orEmpty()
        val statusCode = if (message.contains("Insufficient funds")) {
            BankExceptionStatusCode.INSUFFICIENT_FUNDS
        } else if (message.contains("The supplied token is not valid")) {
            BankExceptionStatusCode.INVALID_TOKEN
        } else if (message.contains("is not an owner of this bank account")) {
            BankExceptionStatusCode.USER_IS_NO_OWNER
        } else if (message.contains("is already an owner of the bank account")) {
            BankExceptionStatusCode.USER_IS_ALREADY_OWNER
        } else if (message.contains("balance is not zero")) {
            BankExceptionStatusCode.BALANCE_NOT_ZERO_SINGLE_OWNER
        } else {
            BankExceptionStatusCode.UNKNOWN_EXCEPTION
        }
        throw BankCommandException(
            exception.message,
            exception,
            statusCode
        )
    }
}