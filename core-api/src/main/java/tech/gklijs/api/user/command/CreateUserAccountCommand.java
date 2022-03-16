package tech.gklijs.api.user.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateUserAccountCommand {
    @TargetAggregateIdentifier
    String username;
    String password;
}
