package edu.uniquindio.stayhub.api.dto.reservation;

import edu.uniquindio.stayhub.api.model.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ReservationRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Accommodation ID is required")
    private Long accommodationId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be greater than zero")
    private Integer numberOfGuests;

    @NotNull(message = "Status is required")
    private ReservationStatus status;
}