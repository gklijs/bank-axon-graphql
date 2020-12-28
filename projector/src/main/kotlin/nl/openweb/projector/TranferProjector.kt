package nl.openweb.projector

import lombok.RequiredArgsConstructor
import nl.openweb.api.bank.event.TransferCompletedEvent
import nl.openweb.api.bank.event.TransferFailedEvent
import nl.openweb.api.bank.event.TransferStartedEvent
import nl.openweb.api.bank.query.TransferResult
import nl.openweb.api.bank.query.TransferResultQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("transfers")
@RequiredArgsConstructor
class TranferProjector(
    val transferRepository: TransferRepository,
    val queryUpdateEmitter: QueryUpdateEmitter,
) {
    @EventHandler
    fun on(event: TransferStartedEvent) {
        val newTransfer = TransferSummary(
            event.transferId,
            event.amount,
            event.from,
            event.to,
            event.description
        )
        transferRepository.save(newTransfer)
    }

    @EventHandler
    fun on(event: TransferFailedEvent) {
        var transfer = transferRepository.findById(event.transferId).orElseThrow()
        transfer.error = event.reason
        transfer.state = TransferSummaryState.FAILED
        transfer = transferRepository.save(transfer)
        queryUpdateEmitter.emit(
            TransferResultQuery::class.java,
            { q -> transfer.transferId.equals(q.transferId) },
            map(transfer)
        )
    }

    @EventHandler
    fun on(event: TransferCompletedEvent) {
        var transfer = transferRepository.findById(event.transferId).orElseThrow()
        transfer.state = TransferSummaryState.COMPLETED
        transfer = transferRepository.save(transfer)
        queryUpdateEmitter.emit(
            TransferResultQuery::class.java,
            { q -> transfer.transferId.equals(q.transferId) },
            map(transfer)
        )
    }

    @QueryHandler
    fun handle(query: TransferResultQuery): TransferResult {
        return transferRepository.findById(query.transferId)
            .map { t -> map(t) }
            .orElse(null)
    }

    private fun map(summary: TransferSummary): TransferResult {
        return TransferResult(
            TransferResult.TransferState.valueOf(summary.state.name),
            summary.error
        )
    }
}