package nl.openweb.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MoneyCreditedEvent {
    @TargetAggregateIdentifier
    String iban;
    Long amount;
    String transferId;
}
