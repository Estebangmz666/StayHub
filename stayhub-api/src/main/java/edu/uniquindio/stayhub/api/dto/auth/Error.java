package edu.uniquindio.stayhub.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for providing standardized error responses.
 * This DTO is used to return a descriptive message and an HTTP status code for API errors.
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Standardized error response DTO")
public class Error {
    /**
     * A human-readable message describing the error.
     */
    @Schema(description = "A message describing the error", example = "The email is already in use")
    private String message;
    /**
     * The HTTP status code of the error.
     */
    @Schema(description = "The HTTP status code of the error", example = "400")
    private int code;
}