package nl.openweb.api.account;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    String id;
    String username;
}
