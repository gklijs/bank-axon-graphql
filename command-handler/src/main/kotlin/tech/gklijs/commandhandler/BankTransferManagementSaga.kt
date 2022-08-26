package tech.gklijs.commandhandler

import org.axonframework.commandhandling.*
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import tech.gklijs.api.bank.command.*
import tech.gklijs.api.bank.error.BankExceptionStatusCode
import tech.gklijs.api.bank.event.*
import tech.gklijs.api.bank.utils.IbanUtil
import java.util.function.Consumer

@Saga
class BankTransferManagementSaga {

    @Transient
    private var commandBus: CommandBus? = null
    private var transferId: String? = null
    private var token: String? = null
    private var amount = 0L
    private var from: String? = null
    private var to: String? = null
    private var description: String? = null
    private var username: String? = null
    private var debited = false

    @Autowired
    fun setCommandBus(commandBus: CommandBus?) {
        this.commandBus = commandBus
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "transferId")
    protected fun on(event: TransferStartedEvent) {
        this.transferId = event.transferId
        this.token = event.token
        this.amount = event.amount
        this.from = event.from
        this.to = event.to
        this.description = event.description
        this.username = event.username

        val command: Any = if (this.to.equals(this.from)) {
            MarkTransferFailedCommand(
                this.transferId,
                BankExceptionStatusCode.FROM_AND_TO_SAME.description
            )
        } else if (IbanUtil.invalidFrom(this.from)) {
            MarkTransferFailedCommand(
                this.transferId,
                BankExceptionStatusCode.INVALID_FROM.description
            )
        } else if ("cash" == this.from || !IbanUtil.isAxonIban(from)) {
            CreditMoneyCommand(this.to, this.amount, this.transferId)
        } else {
            DebitMoneyCommand(
                this.from,
                this.token,
                this.amount,
                this.username,
                this.transferId
            )
        }
        commandBus!!.dispatch(
            GenericCommandMessage.asCommandMessage(command),
            ErrorHandlingCallback { r -> this.handleError(r) })
    }

    @SagaEventHandler(associationProperty = "transferId")
    fun on(event: MoneyDebitedEvent) {
        this.debited = true
        val command: Any = if (IbanUtil.isAxonIban(this.to)) {
            CreditMoneyCommand(this.to, this.amount, this.transferId)
        } else {
            MarkTransferCompletedCommand(this.transferId)
        }
        commandBus!!.dispatch(
            GenericCommandMessage.asCommandMessage(command),
            ErrorHandlingCallback { r -> this.handleError(r) })
    }

    @SagaEventHandler(associationProperty = "transferId")
    fun on(event: MoneyCreditedEvent) {
        val command = MarkTransferCompletedCommand(this.transferId)
        commandBus!!.dispatch(
            GenericCommandMessage.asCommandMessage(command),
            ErrorHandlingCallback { r -> this.handleError(r) })
    }

    @SagaEventHandler(associationProperty = "transferId")
    fun on(event: MoneyReturnedEvent) {
        val command =
            MarkTransferFailedCommand(this.transferId, event.reason)
        commandBus!!.dispatch(
            GenericCommandMessage.asCommandMessage(command),
            ErrorHandlingCallback { r -> this.handleError(r) })
    }

    @SagaEventHandler(associationProperty = "transferId")
    @EndSaga
    fun on(event: TransferCompletedEvent) {
        //nothing left to do
    }

    @SagaEventHandler(associationProperty = "transferId")
    @EndSaga
    fun on(event: TransferFailedEvent) {
        //nothing left to do
    }

    fun handleError(reason: String) {
        val command: Any = if (this.debited) {
            ReturnMoneyCommand(
                this.from,
                this.amount,
                this.transferId,
                reason
            )
        } else {
            MarkTransferFailedCommand(
                this.transferId,
                BankExceptionStatusCode.validDescription(reason)
            )
        }
        commandBus!!.dispatch(
            GenericCommandMessage.asCommandMessage(command),
            ErrorHandlingCallback { r -> this.handleError(r) })
    }

    class ErrorHandlingCallback(
        private val errorHandler: Consumer<String>
    ) : CommandCallback<Any?, Any?> {
        override fun onResult(message: CommandMessage<*>, commandResultMessage: CommandResultMessage<*>) {
            if (commandResultMessage.isExceptional) {
                logger.warn(
                    "Command resulted in exception: {}",
                    message.commandName,
                    commandResultMessage.exceptionResult()
                )
                val statusCode = commandResultMessage.exceptionDetails<BankExceptionStatusCode>()
                    .orElse(BankExceptionStatusCode.UNKNOWN_EXCEPTION)
                val reason =
                    if (commandResultMessage.exceptionResult().message == "The aggregate was not found in the event store") {
                        BankExceptionStatusCode.AXON_BANK_ACCOUNT_NOT_FOUND.description
                    } else {
                        statusCode.description
                    }
                errorHandler.accept(reason)
            } else {
                logger.info("Command executed successfully: {}", message.commandName)
            }
        }

        companion object {
            private val logger = LoggerFactory.getLogger(ErrorHandlingCallback::class.java)
        }
    }
}