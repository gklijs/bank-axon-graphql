package nl.openweb.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class TransferCompletedEvent {
    @TargetAggregateIdentifier
    String transferId;
}
