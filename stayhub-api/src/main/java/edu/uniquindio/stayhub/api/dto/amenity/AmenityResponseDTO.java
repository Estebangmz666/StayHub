package edu.uniquindio.stayhub.api.dto.amenity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for returning an amenity.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Schema(description = "Data Transfer Object for returning an amenity")
public class AmenityResponseDTO {

    /**
     * The unique identifier of the amenity.
     */
    @Schema(description = "The unique identifier of the amenity", example = "1")
    private Long id;

    /**
     * The name of the amenity.
     */
    @Schema(description = "The name of the amenity", example = "Wifi")
    private String name;

    /**
     * A description of the amenity.
     */
    @Schema(description = "A description of the amenity", example = "Free WiFi")
    private String description;

    /**
     * A flag indicating whether the amenity is currently active.
     */
    @Schema(description = "A flag indicating whether the amenity is currently active", example = "true")
    private Boolean active;
}