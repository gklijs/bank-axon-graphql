package nl.openweb.axon.account;

import lombok.Value;

import java.util.UUID;

@Value
public class AccountCreationFailedEvent {
    UUID id;
    String reason;
}
