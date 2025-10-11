package edu.uniquindio.stayhub.api.dto.reservation;

import edu.uniquindio.stayhub.api.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning reservation details to the client.
 * This DTO provides a complete representation of a reservation, including its current status and total price.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for returning reservation details")
public class ReservationResponseDTO {

    /**
     * The unique identifier of the reservation.
     */
    @Schema(description = "The unique identifier of the reservation", example = "1")
    private Long id;

    /**
     * The ID of the guest who made the reservation.
     */
    @Schema(description = "The ID of the guest who made the reservation", example = "10")
    private Long guestId;

    /**
     * The accommodation title of the accommodation that was reserved.
     */
    @Schema(description = "The accommodation title of the accommodation that was reserved")
    private String accommodationTitle;

    /**
     * The ID of the accommodation that was reserved.
     */
    @Schema(description = "The ID of the accommodation that was reserved", example = "25")
    private Long accommodationId;

    /**
     * The check-in date and time.
     */
    @Schema(description = "The check-in date and time", example = "2025-11-20T15:00:00")
    private LocalDateTime checkInDate;

    /**
     * The check-out date and time.
     */
    @Schema(description = "The check-out date and time", example = "2025-11-25T11:00:00")
    private LocalDateTime checkOutDate;

    /**
     * The number of guests for the reservation.
     */
    @Schema(description = "The number of guests for the reservation", example = "2")
    private Integer numberOfGuests;

    /**
     * The total price of the reservation.
     */
    @Schema(description = "The total price of the reservation", example = "250.00")
    private BigDecimal totalPrice;

    /**
     * The current status of the reservation (e.g., PENDING, CONFIRMED, CANCELED).
     */
    @Schema(description = "The current status of the reservation", example = "CONFIRMED")
    private ReservationStatus status;

    /**
     * The date and time the reservation was created.
     */
    @Schema(description = "The date and time the reservation was created", example = "2025-11-15T10:00:00")
    private LocalDateTime createdAt;

    /**
     * The date and time the reservation was last updated.
     */
    @Schema(description = "The date and time the reservation was last updated", example = "2025-11-15T10:00:00")
    private LocalDateTime updatedAt;

    public ReservationResponseDTO(Long reservationId, Long guestId, Long accommodationId, @NotBlank(message = "El título es obligatorio") @Size(max = 100, message = "El título no puede exceder 100 caracteres") String title, ReservationStatus reservationStatus, BigDecimal bigDecimal) {
        this.id = reservationId;
        this.guestId = guestId;
        this.accommodationId = accommodationId;
        this.status = reservationStatus;
        this.totalPrice = bigDecimal;
    }
}