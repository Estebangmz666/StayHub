package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for creating a new accommodation listing.
 * It encapsulates all the necessary details submitted by a host to register a property.
 */
@Setter
@Getter
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating a new accommodation")
public class AccommodationRequestDTO {
    // Getters and setters
    /**
     * The title of the accommodation listing.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "The title of the accommodation", example = "Modern Apartment in the City Center")
    private String title;

    /**
     * A detailed description of the accommodation.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "A detailed description of the accommodation", example = "Spacious and modern apartment with a great view, located in a prime city location.")
    private String description;

    /**
     * The maximum number of guests the accommodation can host.
     */
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    @Schema(description = "The maximum number of guests the accommodation can host", example = "4")
    private Integer capacity;

    /**
     * The longitude coordinate of the accommodation's location.
     */
    @NotNull(message = "Longitude is required")
    @Schema(description = "The longitude coordinate of the accommodation", example = "-75.5668")
    private Double longitude;

    /**
     * The latitude coordinate of the accommodation's location.
     */
    @NotNull(message = "Latitude is required")
    @Schema(description = "The latitude coordinate of the accommodation", example = "6.2442")
    private Double latitude;

    /**
     * A human-readable description of the accommodation's location.
     */
    @NotBlank(message = "Location description is required")
    @Size(max = 200, message = "Location description cannot exceed 200 characters")
    @Schema(description = "A human-readable description of the location", example = "Located two blocks away from El Poblado Park.")
    private String locationDescription;

    /**
     * The city where the accommodation is located.
     */
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Schema(description = "The city where the accommodation is located", example = "Medellin")
    private String city;

    /**
     * The price per night in the local currency.
     */
    @NotNull(message = "Price per night is required")
    @Positive(message = "Price must be greater than zero")
    @Schema(description = "The price per night in the local currency", example = "150.00")
    private BigDecimal pricePerNight;

    /**
     * The URL of the main image for the accommodation listing.
     */
    @URL(message = "Main image must be a valid URL")
    @Schema(description = "The URL of the main image for the accommodation", example = "https://example.com/images/apartment_main.jpg")
    private String mainImage;

    /**
     * A list of URLs for additional images of the accommodation.
     */
    @NotNull(message = "Images list is required")
    @Schema(description = "A list of URLs for additional images of the accommodation")
    private List<@URL(message = "Each image must be a valid URL") String> images;
}