package nl.openweb.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class TransferStartedEvent {
    @TargetAggregateIdentifier
    String transactionId;
    String token;
    long amount;
    String from;
    String to;
    String description;
}
