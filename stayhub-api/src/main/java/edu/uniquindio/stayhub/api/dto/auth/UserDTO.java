package edu.uniquindio.stayhub.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for representing basic user information.
 * This DTO is used to provide a minimal set of user details, often in scenarios
 * like token-related responses.
 */
@Getter
@Setter
@Schema(description = "DTO for basic user information")
public class UserDTO {
    /**
     * The unique identifier of the user.
     */
    @Schema(description = "The unique identifier of the user", example = "1")
    private Long id;
    /**
     * The email of the user.
     */
    @Schema(description = "The email of the user", example = "user@example.com")
    private String email;
}