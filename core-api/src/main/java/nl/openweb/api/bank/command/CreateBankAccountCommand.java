package nl.openweb.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class CreateBankAccountCommand {
    @TargetAggregateIdentifier
    String iban;
    String username;
}
