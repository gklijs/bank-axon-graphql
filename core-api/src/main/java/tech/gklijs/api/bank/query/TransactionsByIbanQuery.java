package tech.gklijs.api.bank.query;

import lombok.Value;

@Value
public class TransactionsByIbanQuery {
    String iban;
    int maxItems;
}
