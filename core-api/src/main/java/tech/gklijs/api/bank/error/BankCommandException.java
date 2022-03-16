package tech.gklijs.api.bank.error;

import org.axonframework.commandhandling.CommandExecutionException;

public class BankCommandException extends CommandExecutionException {

    public BankCommandException(String message, Throwable cause, Object details) {
        super(message, cause, details);
    }
}
