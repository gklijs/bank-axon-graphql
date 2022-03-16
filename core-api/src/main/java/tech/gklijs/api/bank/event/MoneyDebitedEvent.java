package tech.gklijs.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MoneyDebitedEvent {
    @TargetAggregateIdentifier
    String iban;
    Long amount;
    String transferId;
}
