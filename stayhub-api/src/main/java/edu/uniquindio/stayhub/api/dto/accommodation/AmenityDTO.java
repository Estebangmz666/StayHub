package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for representing an amenity.
 * This DTO is used to list the amenities available for an accommodation.
 */
@Getter
@Setter
@Schema(description = "Data Transfer Object for representing an amenity")
public class AmenityDTO {
    /**
     * The unique identifier of the amenity.
     */
    @Schema(description = "The unique identifier of the amenity", example = "1")
    private Long id;
    /**
     * The name of the amenity (e.g., "Wi-Fi", "Swimming Pool").
     */
    @Schema(description = "The name of the amenity", example = "Wi-Fi")
    private String name;
    /**
     * Indicates whether the amenity is currently active or available.
     */
    @Schema(description = "Indicates whether the amenity is currently active", example = "true")
    private boolean isActive;
}