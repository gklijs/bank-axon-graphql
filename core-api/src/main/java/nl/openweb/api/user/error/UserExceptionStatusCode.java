package nl.openweb.api.user.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserExceptionStatusCode {

    INVALID_PASSWORD("Supplied password does not match the password used to create the user"),
    USER_ACCOUNT_NOT_FOUND("The supplied username does not exist"),
    UNKNOWN_EXCEPTION("Something went wrong within the application. Please inform customer service of Open Bank");

    @Getter
    private final String description;
}
