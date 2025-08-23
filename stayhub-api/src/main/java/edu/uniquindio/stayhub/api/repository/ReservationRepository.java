package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCheckInDate(LocalDate date);
}