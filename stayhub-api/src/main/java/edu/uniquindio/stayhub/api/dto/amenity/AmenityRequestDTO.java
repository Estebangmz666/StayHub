package edu.uniquindio.stayhub.api.dto.amenity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for creating a new amenity.
 */
@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Schema(description = "Data Transfer Object for creating a new amenity")
public class AmenityRequestDTO {

    /**
     * The name of the amenity.
     */
    @NotBlank(message = "Name is required")
    @Schema(description = "The name of the amenity", example = "Wifi")
    @JsonProperty("name")
    private String name;

    /**
     * A description of the amenity.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 100, message = "Description cannot exceed 100 characters")
    @Schema(description = "A description of the amenity", example = "Free WiFi")
    @JsonProperty("description")
    private String description;
}