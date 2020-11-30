package nl.openweb.api.balance;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MoneyTransferCommand {
    @TargetAggregateIdentifier
    String id;
    String token;
    long amount;
    String from;
    String to;
    String description;
}
