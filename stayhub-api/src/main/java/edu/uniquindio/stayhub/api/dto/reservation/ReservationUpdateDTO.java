package edu.uniquindio.stayhub.api.dto.reservation;

import edu.uniquindio.stayhub.api.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for updating an existing reservation.
 * It is used to change the status of a reservation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for updating an existing reservation")
public class ReservationUpdateDTO {
    /**
     * The updated status for the reservation (e.g., CONFIRMED, CANCELED).
     */
    @NotNull(message = "Status is required")
    @Schema(description = "The updated status for the reservation", example = "CANCELED")
    private ReservationStatus status;

}