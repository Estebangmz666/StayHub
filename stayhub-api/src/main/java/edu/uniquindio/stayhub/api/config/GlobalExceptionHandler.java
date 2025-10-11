package edu.uniquindio.stayhub.api.config;

import edu.uniquindio.stayhub.api.dto.auth.Error;
import edu.uniquindio.stayhub.api.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========== EXCEPCIONES DE NEGOCIO ==========

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Error> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        LOGGER.warn("Email already exists: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.CONFLICT.value()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> handleUserNotFoundException(UserNotFoundException e) {
        LOGGER.warn("User not found: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Error> handleInvalidPasswordException(InvalidPasswordException e) {
        LOGGER.warn("Invalid password attempt: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Error> handleInvalidTokenException(InvalidTokenException e) {
        LOGGER.warn("Invalid or expired token: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Error> handleIllegalStateException(IllegalStateException e) {
        LOGGER.error("Illegal state: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS.value()),
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(AccommodationNotFoundException.class)
    public ResponseEntity<Error> handleAccommodationNotFoundException(AccommodationNotFoundException e) {
        LOGGER.warn("Accommodation not found: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error(e.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        LOGGER.warn("Validation error: {}", errors);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.error("Illegal argument: {}", e.getMessage());
        return new ResponseEntity<>(
                new Error("Invalid input: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception e) {
        LOGGER.error("Unexpected error occurred", e);
        return new ResponseEntity<>(
                new Error("An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}