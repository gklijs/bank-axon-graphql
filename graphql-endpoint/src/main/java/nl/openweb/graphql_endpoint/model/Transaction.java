package nl.openweb.graphql_endpoint.model;

import lombok.Value;

@Value
public class Transaction {
    int id;
    String changed_by;
    String descr;
    DType direction;
    String from_to;
    String iban;
    String new_balance;
    long amount;
}
