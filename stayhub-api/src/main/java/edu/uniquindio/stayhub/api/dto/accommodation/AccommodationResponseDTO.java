package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for returning accommodation details to the client.
 * It provides a clean, structured representation of an accommodation listing.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for accommodation details returned to the client")
public class AccommodationResponseDTO {

    /**
     * The unique identifier of the accommodation.
     */
    @Schema(description = "The unique identifier of the accommodation", example = "1")
    private Long id;

    /**
     * The title of the accommodation listing.
     */
    @Schema(description = "The title of the accommodation", example = "House in El Poblado")
    private String title;

    /**
     * A detailed description of the accommodation.
     */
    @Schema(description = "A detailed description of the accommodation", example = "Beautiful and spacious house with a great view of the city.")
    private String description;

    /**
     * The maximum number of guests the accommodation can host.
     */
    @Schema(description = "The maximum number of guests the accommodation can host", example = "4")
    private Integer capacity;

    /**
     * The URL of the main image for the accommodation listing.
     */
    @Schema(description = "The URL of the main image for the accommodation", example = "https://example.com/images/house_main.jpg")
    private String mainImage;

    /**
     * The longitude coordinate of the accommodation's location.
     */
    @Schema(description = "The longitude coordinate of the accommodation's location", example = "-75.5674")
    private Double longitude;

    /**
     * The latitude coordinate of the accommodation's location.
     */
    @Schema(description = "The latitude coordinate of the accommodation's location", example = "6.2447")
    private Double latitude;

    /**
     * A human-readable description of the accommodation's location.
     */
    @Schema(description = "A human-readable description of the location", example = "Located in the heart of the city, close to all amenities.")
    private String locationDescription;

    /**
     * The city where the accommodation is located.
     */
    @Schema(description = "The city where the accommodation is located", example = "Medellín")
    private String city;

    /**
     * The price per night in the local currency.
     */
    @Schema(description = "The price per night in the local currency", example = "150000.00")
    private BigDecimal pricePerNight;

    /**
     * A list of URLs for additional images of the accommodation.
     */
    @Schema(description = "A list of URLs for additional images of the accommodation")
    private List<String> images;
}