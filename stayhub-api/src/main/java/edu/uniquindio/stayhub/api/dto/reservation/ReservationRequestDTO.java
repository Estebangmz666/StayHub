package edu.uniquindio.stayhub.api.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a new reservation.
 * This DTO contains all the necessary details to request a reservation for an accommodation.
 */
@Getter
@Setter
@Schema(description = "DTO for creating a new reservation")
public class ReservationRequestDTO {

    /**
     * The ID of the guest making the reservation.
     */
    @NotNull(message = "Guest ID is required")
    @Schema(description = "The ID of the guest making the reservation", example = "10")
    private Long guestId;

    /**
     * The ID of the accommodation to be reserved.
     */
    @NotNull(message = "Accommodation ID is required")
    @Schema(description = "The ID of the accommodation to be reserved", example = "25")
    private Long accommodationId;

    /**
     * The check-in date and time for the reservation.
     */
    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @Schema(description = "The check-in date and time for the reservation", example = "2025-11-20T15:00:00")
    private LocalDateTime checkInDate;

    /**
     * The check-out date and time for the reservation.
     */
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    @Schema(description = "The check-out date and time for the reservation", example = "2025-11-25T11:00:00")
    private LocalDateTime checkOutDate;

    /**
     * The number of guests for the reservation.
     */
    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be greater than zero")
    @Schema(description = "The number of guests for the reservation", example = "2")
    private Integer numberOfGuests;

    public ReservationRequestDTO(Long guestId, Long accommodationId, LocalDateTime checkInDate, LocalDateTime checkOutDate, Integer numberOfGuests) {
        this.guestId = guestId;
        this.accommodationId = accommodationId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
    }
}