package edu.uniquindio.stayhub.api.dto.reservation;

import edu.uniquindio.stayhub.api.model.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationUpdateDTO {
    @NotNull(message = "Status is required")
    private ReservationStatus status;
}