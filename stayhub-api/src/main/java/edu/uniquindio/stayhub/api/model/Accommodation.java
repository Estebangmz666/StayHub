package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "accommodations", indexes = {
        @Index(name = "idx_host_id", columnList = "host_id"),
        @Index(name = "idx_location", columnList = "latitude, longitude"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_deleted", columnList = "isDeleted")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacity;

    @Column
    @URL(message = "La imagen principal debe ser una URL válida")
    private String mainImage;

    @Column(nullable = false)
    @NotNull(message = "La longitud es obligatoria")
    private Double longitude;

    @Column(nullable = false)
    @NotNull(message = "La latitud es obligatoria")
    private Double latitude; //Mapbox Stuff xd

    @Column(nullable = false)
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String locationDescription;

    @Column(nullable = false)
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
    private String city;

    @Column(nullable = false)
    @NotNull(message = "El precio por noche es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal pricePerNight;

    @ElementCollection
    @Column
    @URL(message = "Cada imagen debe ser una URL válida")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToMany
    @JoinTable(
            name = "accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @Builder.Default
    private Set<Amenity> amenities = new HashSet<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @OneToMany(mappedBy = "accommodation")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accommodation")
    private List<Comment> comments = new ArrayList<>();
}