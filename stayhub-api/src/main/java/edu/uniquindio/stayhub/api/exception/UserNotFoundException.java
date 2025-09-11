package edu.uniquindio.stayhub.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Exception thrown when a requested user is not found.
 */
@Schema(description = "Exception thrown when a requested user is not found.")
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {super(message);}
}
