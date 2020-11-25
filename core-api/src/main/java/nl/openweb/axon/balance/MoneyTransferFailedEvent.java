package nl.openweb.axon.balance;

import lombok.Value;

import java.util.UUID;

@Value
public class MoneyTransferFailedEvent {
    UUID id;
    String reason;
}
