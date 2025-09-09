package edu.uniquindio.stayhub.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class AccommodationRequestDTO {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than zero")
    private Integer capacity;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotBlank(message = "Location description is required")
    @Size(max = 200, message = "Location description cannot exceed 200 characters")
    private String locationDescription;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal pricePerNight;

    @URL(message = "Main image must be a valid URL")
    private String mainImage;

    @NotNull(message = "Images list is required")
    private List<@URL(message = "Each image must be a valid URL") String> images;
}