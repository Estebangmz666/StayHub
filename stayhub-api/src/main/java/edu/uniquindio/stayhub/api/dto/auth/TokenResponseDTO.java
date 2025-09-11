package edu.uniquindio.stayhub.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for returning a JWT token after successful authentication.
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "DTO for returning a JWT token")
public class TokenResponseDTO {
    /**
     * The JWT token string.
     */
    @Schema(description = "The JWT token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZXhwIjoxNzI1ODk4MDAwfQ.abc")
    private String token;
}