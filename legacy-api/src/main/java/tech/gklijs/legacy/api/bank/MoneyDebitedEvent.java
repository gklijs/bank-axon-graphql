package tech.gklijs.legacy.api.bank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MoneyDebitedEvent {
    String iban;
    Long amount;
    String from;
    String description;
}
