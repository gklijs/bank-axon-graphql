package nl.openweb.api.user.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RemoveBankAccountCommand {
    @TargetAggregateIdentifier
    String username;
    String iban;
    String token;
}
