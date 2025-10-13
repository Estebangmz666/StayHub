package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for representing an amenity.
 * This DTO is used to list the amenities available for accommodation.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for representing an amenity")
public class AmenityDTO {
    /**
     * The name of the amenity (e.g., "Wi-Fi", "Swimming Pool").
     */
    @Schema(description = "The name of the amenity", example = "Wi-Fi")
    private String name;

    /**
     * Describes the amenity (e.g., "Free Wi-Fi", "Swimming Pool available").
     */
    @Schema(description = "Describes the amenity", example = "Swimming pool, free wifi")
    private String description;
}