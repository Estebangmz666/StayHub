package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing accommodation in the StayHub application.
 * This class maps to the 'accommodations' table in the database and includes
 * all the necessary fields for managing rental properties, their details,
 * and relationships with other entities like users, amenities, and comments.
 */
@Entity
@Table(name = "accommodations", indexes = {
        @Index(name = "idx_host_id", columnList = "host_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accommodation {

    /**
     * The unique identifier for the accommodation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The host (owner) of the accommodation.
     * This is a many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    @NotNull(message = "El anfitrión es obligatorio")
    private User host;

    /**
     * The title of the accommodation listing.
     */
    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    /**
     * A detailed description of the accommodation.
     */
    @Column(nullable = false)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    /**
     * The maximum number of guests the accommodation can host.
     */
    @Column(nullable = false)
    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor que cero")
    private Integer capacity;

    /**
     * The price per night for the accommodation.
     */
    @Column(nullable = false)
    @NotNull(message = "El precio por noche es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private BigDecimal pricePerNight;

    /**
     * The URL of the main image for the accommodation listing.
     */
    @Column(name = "main_image")
    @URL(message = "La imagen principal debe ser una URL válida")
    private String mainImage;

    /**
     * The longitude coordinate of the accommodation's location.
     */
    @Column(nullable = false)
    @NotNull(message = "La longitud es obligatoria")
    private Double longitude;

    /**
     * The latitude coordinate of the accommodation's location.
     */
    @Column(nullable = false)
    @NotNull(message = "La latitud es obligatoria")
    private Double latitude;

    /**
     * A description of the location, such as neighborhood or landmarks.
     */
    @Column(name = "location_description", nullable = false)
    @NotBlank(message = "La descripción de la ubicación es obligatoria")
    @Size(max = 200, message = "La descripción de la ubicación no puede exceder 200 caracteres")
    private String locationDescription;

    /**
     * The city where the accommodation is located.
     */
    @Column(nullable = false)
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 50, message = "La ciudad no puede exceder 50 caracteres")
    private String city;

    /**
     * A list of URLs for additional images of the accommodation.
     */
    @ElementCollection
    @CollectionTable(name = "accommodation_images", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image_url")
    @NotNull(message = "La lista de imágenes es obligatoria")
    private List<@URL(message = "Cada imagen debe ser una URL válida") String> images;

    /**
     * A list of amenities available at the accommodation.
     * This is a many-to-many relationship with the Amenity entity.
     */
    @ManyToMany
    @JoinTable(
            name = "accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    /**
     * A list of reservations made for this accommodation.
     * This is a one-to-many relationship with the Reservation entity.
     */
    @OneToMany(mappedBy = "accommodation")
    private List<Reservation> reservations;

    /**
     * A list of comments made about this accommodation.
     * This is a one-to-many relationship with the Comment entity.
     */
    @OneToMany(mappedBy = "accommodation")
    private List<Comment> comments;

    /**
     * A soft-delete flag. If true, the accommodation is considered deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    /**
     * The timestamp for when the entity was created.
     * Automatically managed by Hibernate's @CreationTimestamp.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp for the last update to the entity.
     * Automatically managed by Hibernate's @UpdateTimestamp.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Accommodation(String title, User host) {
        this.title = title;
        this.host = host;
    }
}