package edu.uniquindio.stayhub.api.dto.auth.passwordReset;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Schema(description = "DTO for resetting a password with a token")
public class ResetPasswordDTO {
    @NotBlank(message = "Token is required")
    @Schema(description = "The password reset token", example = "a1b2bc3d4e5f6g7")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long with uppercase and numbers")
    @Schema(description = "The new password for the account", example = "Str0ngP4ssw0rd")
    private String newPassword;
}