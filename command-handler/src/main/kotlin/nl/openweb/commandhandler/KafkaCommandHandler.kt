package nl.openweb.commandhandler

import nl.openweb.data.*
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Predicate
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.handler.annotation.SendTo

@EnableBinding(CommandHandlerTopics::class)
@EnableAutoConfiguration
class KafkaCommandHandler(
        private val accountCreationProcessor: AccountCreationProcessor,
        private val moneyTransferProcessor: MoneyTransferProcessor
) {

    @StreamListener(CommandHandlerTopics.COM)
    @SendTo(CommandHandlerTopics.ACF, CommandHandlerTopics.MTF, CommandHandlerTopics.BACH)
    fun on(input: KStream<String, SpecificRecord>): Array<out KStream<String, SpecificRecord>>? {
        val isAccountCreationFeedback = Predicate { _: String, v: SpecificRecord -> v is AccountCreationConfirmed || v is AccountCreationFailed }
        val isMoneyTransferFeedback = Predicate { _: String, v: SpecificRecord -> v is MoneyTransferConfirmed || v is MoneyTransferFailed }
        val isBalance = Predicate { _: String, v: SpecificRecord -> v is BalanceChanged }

        return input
                .flatMap(this::getResponses)
                .branch(isAccountCreationFeedback, isMoneyTransferFeedback, isBalance)
    }

    private fun getResponses(k: String, v: SpecificRecord): List<KeyValue<String, SpecificRecord>> {
        if (v is ConfirmAccountCreation) {
            return listOf(KeyValue(k, accountCreationProcessor.getResponse(v)))
        }
        if (v is ConfirmMoneyTransfer) {
            return moneyTransferProcessor.getResponses(k, v)
        }
        throw RuntimeException("Unknown command: " + v.javaClass.simpleName)
    }
}