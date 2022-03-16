package tech.gklijs.api.bank.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class UserAddedEvent {
    @TargetAggregateIdentifier
    String username;
    String iban;
}
