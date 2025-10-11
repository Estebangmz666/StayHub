package edu.uniquindio.stayhub.api.dto.auth.passwordReset;

import edu.uniquindio.stayhub.api.dto.auth.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for a password-reset token.
 * This DTO is used to represent the state of a password reset token,
 * including its association with a user and its expiration.
 */
@Getter
@Setter
@Schema(description = "DTO for a password reset token")
public class PasswordResetTokenDTO {
    /**
     * The unique identifier of the token.
     */
    @Schema(description = "The unique identifier of the token", example = "1")
    private Long id;
    /**
     * The token string itself.
     */
    @Schema(description = "The token string", example = "a1b2c3d4e5f6g7h8")
    private String token;
    /**
     * The user associated with this token.
     */
    @Schema(description = "The user associated with this token")
    private UserDTO user;
    /**
     * The date and time when the token expires.
     */
    @Schema(description = "The date and time when the token expires", example = "2025-01-20T10:00:00")
    private LocalDateTime expiryDate;
    /**
     * A flag indicating if the token has been used.
     */
    @Schema(description = "A flag indicating if the token has been used", example = "false")
    private boolean used;
}