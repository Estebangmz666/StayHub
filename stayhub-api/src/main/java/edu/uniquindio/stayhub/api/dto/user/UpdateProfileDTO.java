package edu.uniquindio.stayhub.api.dto.user;

import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class UpdateProfileDTO {
    @NotNull(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotNull(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "El número de teléfono debe tener el formato colombiano: +57 seguido de 10 dígitos")
    private String phoneNumber;

    @Nullable
    private LocalDate birthDate;

    @Nullable
    @URL(message = "La foto de perfil debe ser una URL válida")
    private String profilePicture;

    @Nullable
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Nullable
    @Valid
    private List<@URL(message = "Cada documento legal debe tener una url válida") String> legalDocuments;

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