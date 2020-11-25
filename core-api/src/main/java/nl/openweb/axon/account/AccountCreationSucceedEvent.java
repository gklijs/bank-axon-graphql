package nl.openweb.axon.account;

import lombok.Value;

import java.util.UUID;

@Value
public class AccountCreationSucceedEvent {
    UUID id;
    String iban;
    String token;
}
