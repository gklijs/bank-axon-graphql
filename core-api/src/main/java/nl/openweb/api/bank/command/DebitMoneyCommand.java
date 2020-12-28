package nl.openweb.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class DebitMoneyCommand {
    @TargetAggregateIdentifier
    String iban;
    String token;
    Long amount;
    String username;
    String transferId;
}
