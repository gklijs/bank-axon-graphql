package tech.gklijs.api.bank.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankExceptionStatusCode {

    INVALID_TOKEN("Supplied token is not valid, does not match the stored token"),
    USER_IS_NO_OWNER("User is no owner of the bank account, so could not be removed"),
    USER_IS_ALREADY_OWNER("User is already owner of the bank account, so could not be added"),
    INSUFFICIENT_FUNDS("Could not debit the requested amount because the resulting balance would be below the minimum"),
    BALANCE_NOT_ZERO_SINGLE_OWNER(
            "Could not remove the bank account as there is only one owner, and the balance is not zero"),
    AXON_BANK_ACCOUNT_NOT_FOUND(
            "The used iban to send transfer the money to was a valid axon bank IBAN, but does not exist, it might be closed"),
    INVALID_FROM("From is invalid"),
    FROM_AND_TO_SAME("From and to can't be same for transfer"),
    BANK_ACCOUNT_NOT_FOUND("The supplied iban does not exist"),
    UNKNOWN_EXCEPTION("Something went wrong within the application. Please inform customer service of Axon Bank");

    @Getter
    private final String description;

    public static String validDescription(String description) {
        for (BankExceptionStatusCode code : BankExceptionStatusCode.values()) {
            if (description.equals(code.getDescription())) {
                return description;
            }
        }
        return UNKNOWN_EXCEPTION.getDescription();
    }
}
