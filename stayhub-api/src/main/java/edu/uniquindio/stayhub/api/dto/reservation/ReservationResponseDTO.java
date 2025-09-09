package edu.uniquindio.stayhub.api.dto.reservation;

import edu.uniquindio.stayhub.api.model.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ReservationResponseDTO {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private ReservationStatus status;
}