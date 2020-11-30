package nl.openweb.api.balance;

import lombok.Value;

@Value
public class BalanceChangedEvent {
    String iban;
    long newBalance;
    long changedBy;
    String fromTo;
    String description;
}
