package tech.gklijs.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class TransferStartedEvent {
    @TargetAggregateIdentifier
    String transferId;
    String token;
    long amount;
    String from;
    String to;
    String description;
    String username;
}
