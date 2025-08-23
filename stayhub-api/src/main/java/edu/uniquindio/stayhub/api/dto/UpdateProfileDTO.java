package edu.uniquindio.stayhub.api.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

public class UpdateProfileDTO {
    @NotNull(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "El número de teléfono debe tener el formato colombiano: +57 seguido de 10 dígitos")
    private String phoneNumber;

    @URL(message = "Debe ser una URL valida")
    @Nullable
    private String profilePicture;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @Valid
    private List<String> legalDocuments;
}