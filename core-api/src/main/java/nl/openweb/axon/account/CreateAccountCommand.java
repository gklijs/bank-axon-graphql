package nl.openweb.axon.account;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Value
public class CreateAccountCommand {
    @TargetAggregateIdentifier
    UUID id;
    String username;
}
