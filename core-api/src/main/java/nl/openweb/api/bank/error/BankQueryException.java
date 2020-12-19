package nl.openweb.api.bank.error;

import org.axonframework.queryhandling.QueryExecutionException;

public class BankQueryException extends QueryExecutionException {

    public BankQueryException(String message, Throwable cause, Object details) {
        super(message, cause, details);
    }
}
