package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationDAO {
    private final ReservationRepository reservationRepository;

    public ReservationDAO(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation saveReservation(Reservation reservation) {
        LocalDateTime checkIn = reservation.getCheckInDate();
        LocalDateTime checkOut = reservation.getCheckOutDate();
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findActiveByUserId(Long userId) {
        return reservationRepository.findByGuestIdAndDeletedFalse(userId);
    }
}