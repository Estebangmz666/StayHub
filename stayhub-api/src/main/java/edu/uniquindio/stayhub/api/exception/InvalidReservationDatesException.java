package edu.uniquindio.stayhub.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Exception thrown when the requested reservation dates are invalid.
 */
@Schema(description = "Exception thrown when the requested reservation dates are invalid.")
public class InvalidReservationDatesException extends RuntimeException {
    public InvalidReservationDatesException(String message) {
        super(message);
    }
}