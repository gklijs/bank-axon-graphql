package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.event.TransferCompletedEvent
import nl.openweb.api.bank.event.TransferFailedEvent
import nl.openweb.api.bank.event.TransferStartedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class TranferProjector(
    val transferRepository: TransferRepository,
) {
    @EventHandler
    fun on(event: TransferStartedEvent) {
        val newTransfer = TransferSummary(
            event.transactionId,
            event.amount,
            event.from,
            event.to,
            event.description
        )
        transferRepository.save(newTransfer)
    }

    @EventHandler
    fun on(event: TransferFailedEvent) {
        val transfer = transferRepository.findById(event.transactionId).orElseThrow()
        transfer.error = event.reason
        transfer.state = TransferState.FAILED
        transferRepository.save(transfer)
    }

    @EventHandler
    fun on(event: TransferCompletedEvent) {
        val transfer = transferRepository.findById(event.transactionId).orElseThrow()
        transfer.state = TransferState.COMPLETED
        transferRepository.save(transfer)
    }
}