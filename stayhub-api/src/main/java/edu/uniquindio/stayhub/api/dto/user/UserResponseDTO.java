package edu.uniquindio.stayhub.api.dto.user;

import edu.uniquindio.stayhub.api.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for returning user details to the client.
 * This DTO provides a complete representation of a user's profile.
 */
@Getter
@Setter
@Schema(description = "DTO for returning user details")
public class UserResponseDTO {

    /**
     * The unique identifier of the user.
     */
    @Schema(description = "The unique identifier of the user", example = "1")
    private Long id;

    /**
     * The user's email address.
     */
    @Schema(description = "The user's email address", example = "john.doe@example.com")
    private String email;

    /**
     * The user's full name.
     */
    @Schema(description = "The user's full name", example = "John Doe")
    private String name;

    /**
     * The user's phone number.
     */
    @Schema(description = "The user's phone number", example = "+57 3101234567")
    private String phoneNumber;

    /**
     * The user's birthdate.
     */
    @Schema(description = "The user's birth date", example = "1995-08-20")
    private LocalDate birthDate;

    /**
     * The role of the user (e.g., GUEST, HOST).
     */
    @Schema(description = "The role of the user", example = "GUEST")
    private Role role;

    /**
     * A URL to the user's profile picture.
     */
    @Schema(description = "A URL to the user's profile picture", example = "https://example.com/profile/picture.jpg")
    private String profilePicture;

    /**
     * The description of the user, specific to hosts.
     */
    @Schema(description = "A description of the user, specific to hosts", example = "I'm a host who loves to travel and meet new people.")
    private String description;

    /**
     * A list of URLs to legal documents, specific to hosts.
     */
    @Schema(description = "A list of URLs to legal documents, specific to hosts", example = "[\"https://example.com/legal/doc1.pdf\"]")
    private List<String> legalDocuments;
}