package edu.uniquindio.stayhub.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Exception thrown when a password does not meet the specified criteria.
 */
@Schema(description = "Exception thrown when a password does not meet the specified criteria.")
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) { super(message); }
}