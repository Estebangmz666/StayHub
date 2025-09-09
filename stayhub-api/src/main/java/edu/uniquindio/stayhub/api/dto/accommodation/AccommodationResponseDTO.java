package edu.uniquindio.stayhub.api.dto.accommodation;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
public class AccommodationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer capacity;
    private String mainImage;
    private Double longitude;
    private Double latitude;
    private String locationDescription;
    private String city;
    private BigDecimal pricePerNight;
    private List<String> images;
}