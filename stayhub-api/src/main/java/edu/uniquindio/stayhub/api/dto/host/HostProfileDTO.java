package edu.uniquindio.stayhub.api.dto.host;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for updating a host's profile information.
 * This DTO is used to modify a host's public profile details.
 */
@Getter
@Setter
@Schema(description = "DTO for updating a host's profile information")
public class HostProfileDTO {
    /**
     * The updated detailed description of the host.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "The updated detailed description of the host", example = "I am a passionate host who loves to share my culture and city with travelers. I've been hosting for five years and enjoy meeting people from all over the world.")
    private String description;
}