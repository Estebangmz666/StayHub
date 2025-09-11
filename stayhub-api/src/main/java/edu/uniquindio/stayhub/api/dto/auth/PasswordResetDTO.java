package edu.uniquindio.stayhub.api.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for initiating a password reset request.
 * It contains the user's email and the new password.
 */
@Getter
@Setter
@Schema(description = "DTO for resetting a user's password")
public class PasswordResetDTO {
    /**
     * The email of the user requesting the password reset.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "The email of the user", example = "user@example.com")
    private String email;

    /**
     * The new password for the user's account.
     */
    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters with uppercase and numbers")
    @Schema(description = "The new password for the account", example = "StrongP@ssw0rd")
    private String newPassword;
}