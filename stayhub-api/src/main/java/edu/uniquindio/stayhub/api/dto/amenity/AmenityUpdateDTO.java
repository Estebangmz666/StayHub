package edu.uniquindio.stayhub.api.dto.amenity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for updating an amenity.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Schema(description = "DTO for updating an amenity")
public class AmenityUpdateDTO {

    @Schema(description = "The new name of the amenity", example = "Pool")
    private String name;

    @Size(max = 100)
    @Schema(description = "The new description of the amenity", example = "Bigger swimming pools")
    private String description;

    @Schema(description = "The new status of the amenity", example = "true")
    private Boolean active;
}