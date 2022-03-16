package tech.gklijs.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class AddUserCommand {
    @TargetAggregateIdentifier
    String iban;
    String username;
    String token;
}
