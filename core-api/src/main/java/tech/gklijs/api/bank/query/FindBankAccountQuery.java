package tech.gklijs.api.bank.query;

import lombok.Value;

@Value
public class FindBankAccountQuery {
    String iban;
}
