package nl.openweb.api.account;

import lombok.Value;

@Value
public class AccountCreationSucceedEvent {
    String id;
    String username;
    String iban;
    String token;
}
