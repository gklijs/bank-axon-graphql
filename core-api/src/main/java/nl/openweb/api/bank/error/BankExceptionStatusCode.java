package nl.openweb.api.bank.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankExceptionStatusCode {

    INVALID_TOKEN("Supplied token is not valid, does not match the stored token"),
    USER_IS_NO_OWNER("User is no owner of the bank account, so could not be removed"),
    INSUFFICIENT_FUNDS("Could not debit the requested amount because the resulting baland would be below the minimum"),
    BALANCE_NOT_ZERO_SINGLE_OWNER("Could not remove the baank account as there is only one owner, and the balance is not zero"),
    OPEN_BANK_ACCOUNT_NOT_FOUND("The used iban to send tranfer the money to was an open bank IBAN, but does not exist, it might be closed"),
    INVALID_IBAN("The supplied iban was not valid"),
    BANK_ACCOUNT_NOT_FOUND("The supplied iban does not exist"),
    UNKNOWN_EXCEPTION("Something went wrong within the application. Please inform customer service of Open Bank");

    @Getter
    private final String description;
}
