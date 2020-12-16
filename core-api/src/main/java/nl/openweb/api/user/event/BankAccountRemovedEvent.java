package nl.openweb.api.user.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class BankAccountRemovedEvent {
    @TargetAggregateIdentifier
    String username;
    String iban;
}
