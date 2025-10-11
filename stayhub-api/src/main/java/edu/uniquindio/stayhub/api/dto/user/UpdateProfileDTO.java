package edu.uniquindio.stayhub.api.dto.user;

import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for updating a user's profile.
 * This DTO allows for modifying user details and includes fields specific to hosts.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Schema(description = "DTO for updating a user's profile")
public class UpdateProfileDTO {

    /**
     * The user's updated name.
     */
    @NotNull(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Schema(description = "The user's updated name", example = "Juan Pérez")
    private String name;

    /**
     * The user's updated phone number in Colombian format.
     */
    @NotNull(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "El número de teléfono debe tener el formato colombiano: +57 seguido de 10 dígitos")
    @Schema(description = "The user's updated phone number in Colombian format", example = "+57 3101234567")
    private String phoneNumber;

    /**
     * The user's birthdate.
     */
    @Nullable
    @Schema(description = "The user's birth date", example = "1990-05-15")
    private LocalDate birthDate;

    /**
     * A URL to the user's profile picture.
     */
    @Nullable
    @URL(message = "La foto de perfil debe ser una URL válida")
    @Schema(description = "A URL to the user's profile picture", example = "https://example.com/profile/picture.jpg")
    private String profilePicture;

    /**
     * A description of the user, primarily for host profiles.
     */
    @Nullable
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "A description of the user, primarily for host profiles", example = "I'm a host who loves to travel and meet new people. I offer a cozy and comfortable stay.")
    private String description;

    /**
     * A list of URLs to legal documents, required for host verification.
     */
    @Nullable
    @Valid
    @Schema(description = "A list of URLs to legal documents, required for host verification", example = "[\"https://example.com/legal/document1.pdf\", \"https://example.com/legal/document2.pdf\"]")
    private List<@URL(message = "Cada documento legal debe tener una url válida") String> legalDocuments;

    public UpdateProfileDTO(String name, @org.jetbrains.annotations.Nullable String description, String phoneNumber, Object o, Object o1) {
        this.name = name;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }

    @AfterMapping
    void mapHostProfile(@MappingTarget User user, UpdateProfileDTO dto) {
        if ((dto.getDescription() != null || dto.getLegalDocuments() != null) && user.getRole() == Role.HOST) {
            if (user.getHostProfile() == null) {
                user.setHostProfile(new HostProfile());
                user.getHostProfile().setUser(user);
            }

            if (dto.getDescription() != null) {
                user.getHostProfile().setDescription(dto.getDescription());
            }

            if (dto.getLegalDocuments() != null) {
                user.getHostProfile().setLegalDocuments(dto.getLegalDocuments());
            }
        }
    }
}