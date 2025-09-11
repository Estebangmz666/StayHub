package edu.uniquindio.stayhub.api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object for user login credentials.
 */
@Data
@Schema(description = "DTO for user login credentials")
public class UserLoginDTO {

    /**
     * The user's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "The user's email address", example = "jane.doe@example.com")
    private String email;

    /**
     * The user's password.
     */
    @NotBlank(message = "Password is required")
    @Schema(description = "The user's password", example = "MyP@ssword123")
    private String password;
}