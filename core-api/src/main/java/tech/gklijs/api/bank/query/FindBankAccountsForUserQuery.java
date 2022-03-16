package tech.gklijs.api.bank.query;

import lombok.Value;

@Value
public class FindBankAccountsForUserQuery {
    String username;
}
