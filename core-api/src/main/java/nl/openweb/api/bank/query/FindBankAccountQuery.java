package nl.openweb.api.bank.query;

import lombok.Value;

@Value
public class FindBankAccountQuery {
    String iban;
}
