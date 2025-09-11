package edu.uniquindio.stayhub.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Exception thrown when a provided authentication token is invalid.
 */
@Schema(description = "Exception thrown when a provided authentication token is invalid.")
public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) { super(message); }
}