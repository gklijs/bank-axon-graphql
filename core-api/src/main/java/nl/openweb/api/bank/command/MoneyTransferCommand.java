package nl.openweb.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MoneyTransferCommand {
    @TargetAggregateIdentifier
    String transactionId;
    String token;
    long amount;
    String from;
    String to;
    String description;
}
