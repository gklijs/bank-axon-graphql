package nl.openweb.api.account;

import lombok.Value;

import javax.persistence.Id;

@Value
public class AccountSummary {
    @Id
    String id;
    String username;
    String iban;
    String token;
    int balance;
}
