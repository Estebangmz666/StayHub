package edu.uniquindio.stayhub.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Exception thrown when requested accommodation is not found.
 */
@Schema(description = "Exception thrown when a requested accommodation is not found.")
public class AccommodationNotFoundException extends RuntimeException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
}