package tech.gklijs.api.bank.query;

import lombok.Value;

@Value
public class BankAccount {
    String iban;
    String token;
    Long balance;
}
