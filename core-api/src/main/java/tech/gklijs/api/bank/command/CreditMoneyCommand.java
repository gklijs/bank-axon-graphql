package tech.gklijs.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreditMoneyCommand {
    @TargetAggregateIdentifier
    String iban;
    Long amount;
    String transferId;
}
