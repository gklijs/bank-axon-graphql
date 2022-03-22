package tech.gklijs.api.bank.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class BankAccountCreatedEvent {
    @TargetAggregateIdentifier
    String iban;
    String token;
    String username;
}
