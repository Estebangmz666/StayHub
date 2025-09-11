package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a reservation in the StayHub application.
 * This class includes validation constraints to ensure data integrity
 * before persistence.
 */
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_guest_id", columnList = "guest_id"),
        @Index(name = "idx_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_check_in_date", columnList = "check_in_date"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@AssertTrue(message = "La fecha de check-out debe ser posterior al check-in")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    /**
     * The unique identifier for the reservation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who is the guest for this reservation.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    @NotNull(message = "El huésped es obligatorio")
    private User guest;

    /**
     * The accommodation being reserved.
     * This field is mandatory and cannot be null.
     */
    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    @NotNull(message = "El alojamiento es obligatorio")
    private Accommodation accommodation;

    /**
     * The date and time when the guest checks in.
     * Must be today or in the future.
     */
    @Column(name = "check_in_date", nullable = false)
    @NotNull(message = "La fecha de check-in es obligatoria")
    @FutureOrPresent(message = "La fecha de check-in debe ser hoy o en el futuro")
    private LocalDateTime checkInDate;

    /**
     * The date and time when the guest checks out.
     * Must be in the future and after the check-in date.
     */
    @Column(name = "check_out_date", nullable = false)
    @NotNull(message = "La fecha de check-out es obligatoria")
    @Future(message = "La fecha de check-out debe ser en el futuro")
    private LocalDateTime checkOutDate;

    /**
     * The number of guests for the reservation.
     * Must be at least 1.
     */
    @Column(name = "number_of_guests", nullable = false)
    @NotNull(message = "El número de huéspedes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    private Integer numberOfGuests;

    /**
     * The total price for the reservation.
     * Must be a positive value.
     */
    @Column(nullable = false)
    @NotNull(message = "El precio total es obligatorio")
    @Positive(message = "El precio total debe ser positivo")
    private BigDecimal totalPrice;

    /**
     * The status of the reservation (e.g., CONFIRMED, PENDING, CANCELLED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private ReservationStatus status;

    /**
     * A flag to indicate if the reservation has been soft-deleted.
     * Defaults to false.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    /**
     * The timestamp of when the reservation was created.
     * This is automatically set and cannot be updated.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp of when the reservation was last updated.
     * This is automatically updated on each modification.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Validation method to ensure the check-out date is after the check-in date.
     *
     * @return true if check-out is after check-in, false otherwise.
     */
    public boolean isCheckOutAfterCheckIn() {
        if (checkInDate == null || checkOutDate == null) return true;
        return checkOutDate.isAfter(checkInDate);
    }
}