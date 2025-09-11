package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for updating an existing accommodation.
 * It encapsulates the details that can be modified by a host.
 */
@Setter
@Getter
@Schema(description = "Data Transfer Object for updating an existing accommodation")
public class AccommodationUpdateDTO {
    // Getters and setters
    /**
     * The updated title of the accommodation listing.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "The updated title of the accommodation", example = "Modern Apartment in the City Center")
    private String title;

    /**
     * The updated detailed description of the accommodation.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "The updated detailed description of the accommodation", example = "A newly renovated apartment with brand-new appliances and furniture.")
    private String description;

    /**
     * The updated maximum number of guests the accommodation can host.
     */
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    @Schema(description = "The updated maximum number of guests the accommodation can host", example = "4")
    private Integer capacity;

    /**
     * The updated longitude coordinate of the accommodation's location.
     */
    @NotNull(message = "Longitude is required")
    @Schema(description = "The updated longitude coordinate of the accommodation", example = "-75.5668")
    private Double longitude;

    /**
     * The updated latitude coordinate of the accommodation's location.
     */
    @NotNull(message = "Latitude is required")
    @Schema(description = "The updated latitude coordinate of the accommodation", example = "6.2442")
    private Double latitude;

    /**
     * The updated human-readable description of the accommodation's location.
     */
    @NotBlank(message = "Location description is required")
    @Size(max = 200, message = "Location description cannot exceed 200 characters")
    @Schema(description = "The updated human-readable description of the location", example = "Located close to the city's main historical sites.")
    private String locationDescription;

    /**
     * The updated city where the accommodation is located.
     */
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Schema(description = "The updated city where the accommodation is located", example = "Medellin")
    private String city;

    /**
     * The updated price per night in the local currency.
     */
    @NotNull(message = "Price per night is required")
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "The updated price per night in the local currency", example = "175.00")
    private BigDecimal pricePerNight;

    /**
     * The updated URL of the main image for the accommodation listing.
     */
    @URL(message = "Main image must be a valid URL")
    @Schema(description = "The updated URL of the main image for the accommodation", example = "https://example.com/images/apartment_main_v2.jpg")
    private String mainImage;

    /**
     * The updated list of URLs for additional images of the accommodation.
     */
    @Schema(description = "The updated list of URLs for additional images of the accommodation")
    private List<@URL(message = "Each image must be a valid URL") String> images;
}