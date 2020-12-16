package nl.openweb.api.user.query;

import lombok.Value;
import nl.openweb.api.bank.query.BankAccount;

import java.util.List;

@Value
public class UserAccount {
    String username;
    String password;
    List<BankAccount> bankAccounts;
}
