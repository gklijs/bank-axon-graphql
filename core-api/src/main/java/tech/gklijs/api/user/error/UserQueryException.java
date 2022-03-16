package tech.gklijs.api.user.error;

import org.axonframework.queryhandling.QueryExecutionException;

public class UserQueryException extends QueryExecutionException {

    public UserQueryException(String message, Throwable cause, Object details) {
        super(message, cause, details);
    }
}
