package tech.gklijs.api.bank.query;

import lombok.Value;

@Value
public class Transaction {
    long id;
    String iban;
    String fromTo;
    long changedBy;
    long newBalance;
    String description;
}
