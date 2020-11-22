package nl.openweb.commandhandler

import nl.openweb.data.BalanceChanged
import nl.openweb.data.ConfirmMoneyTransfer
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.streams.kstream.KStream
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output

interface CommandHandlerTopics {
    @Input(COM)
    fun input(): KStream<String, SpecificRecord>

    @Output(ACF)
    fun accountCreationFeedback(): KStream<String, SpecificRecord>

    @Output(MTF)
    fun moneyTransferFeedback(): KStream<String, SpecificRecord>

    @Output(BACH)
    fun balance(): KStream<String, BalanceChanged>

    companion object {
        const val COM = "com"
        const val ACF = "acf"
        const val MTF = "mtf"
        const val BACH = "bach"
    }
}