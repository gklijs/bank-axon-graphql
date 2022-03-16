package tech.gklijs.api.user.error;

import org.axonframework.commandhandling.CommandExecutionException;

public class UserCommandException extends CommandExecutionException {

    public UserCommandException(String message, Throwable cause, Object details) {
        super(message, cause, details);
    }
}
