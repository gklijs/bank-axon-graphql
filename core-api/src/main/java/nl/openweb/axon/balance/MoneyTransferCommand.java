package nl.openweb.axon.balance;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
public class MoneyTransferCommand {
    @TargetAggregateIdentifier
    UUID id;
    String token;
    long amount;
    String from;
    String to;
    String description;
}
