package nl.openweb.api.balance;

import lombok.Value;

@Value
public class MoneyTransferFailedEvent {
    String id;
    String reason;
}
