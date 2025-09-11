package edu.uniquindio.stayhub.api.dto.user;

import edu.uniquindio.stayhub.api.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Data Transfer Object for user registration.
 * This DTO contains all the necessary information to create a new user account.
 */
@Getter
@Setter
@Schema(description = "DTO for user registration")
public class UserRegistrationDTO {

    /**
     * The user's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Schema(description = "The user's email address", example = "john.doe@example.com")
    private String email;

    /**
     * The user's password.
     */
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters with uppercase and numbers")
    @Schema(description = "The user's password", example = "P@ssw0rd123")
    private String password;

    /**
     * The user's full name.
     */
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "The user's full name", example = "John Doe")
    private String name;

    /**
     * The user's phone number in Colombian format.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "Phone number must follow Colombian format: +57 followed by 10 digits")
    @Schema(description = "The user's phone number in Colombian format", example = "+57 3101234567")
    private String phoneNumber;

    /**
     * The user's birth date.
     */
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @Schema(description = "The user's birth date", example = "1995-08-20")
    private LocalDate birthDate;

    /**
     * The role of the user (e.g., GUEST, HOST).
     */
    @NotNull(message = "Role is required")
    @Schema(description = "The role of the user", example = "GUEST")
    private Role role;
}