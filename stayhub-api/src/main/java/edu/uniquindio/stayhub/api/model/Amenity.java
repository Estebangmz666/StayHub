package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing an amenity (service or feature) of accommodation.
 * This class maps to the 'amenities' table in the database.
 */
@Entity
@Table(name = "amenities")
@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor
public class Amenity extends Auditable{
    /**
     * The unique identifier for the amenity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the amenity, which must be unique.
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 50, message = "El nombre no puede exceder 50 caracteres")
    private String name;

    /**
     * A flag indicating whether the amenity is currently active.
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;
}