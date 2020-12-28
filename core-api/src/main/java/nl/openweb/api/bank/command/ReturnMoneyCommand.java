package nl.openweb.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class ReturnMoneyCommand {
    @TargetAggregateIdentifier
    String iban;
    Long amount;
    String transferId;
    String reason;
}
