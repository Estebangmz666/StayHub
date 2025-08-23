package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accommodations", indexes = {
        @Index(name = "idx_host_id", columnList = "host_id"),
        @Index(name = "idx_location", columnList = "latitude, longitude")
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

    @Column(nullable = false)
    @NotNull(message = "La longitud es obligatoria")
    private Double longitude;

    @Column(nullable = false)
    @NotNull(message = "La latitud es obligatoria")
    private Double latitude; //Mapbox Stuff xd

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

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @OneToMany(mappedBy = "accommodation")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accommodation")
    private List<Comment> comments = new ArrayList<>();
}