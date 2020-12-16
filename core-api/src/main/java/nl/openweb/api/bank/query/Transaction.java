package nl.openweb.api.bank.query;

import lombok.Value;

@Value
public class Transaction {
    Integer id;
    String transactionId;
    String token;
    long amount;
    String from;
    String to;
    String description;
}
