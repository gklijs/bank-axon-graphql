package nl.openweb.api.account;

import lombok.Value;

@Value
public class AccountCreationFailedEvent {
    String id;
    String username;
    String reason;
}
