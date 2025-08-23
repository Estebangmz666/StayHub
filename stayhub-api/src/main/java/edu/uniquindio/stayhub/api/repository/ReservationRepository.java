package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Reservation;

import java.time.LocalDate;
import java.util.List;

public class ReservationRepository {
    List<Reservation> findByCheckInDate(LocalDate date) {
        return null;
    }
}