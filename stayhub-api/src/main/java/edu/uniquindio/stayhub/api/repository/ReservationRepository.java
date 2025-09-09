package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCheckInDate(LocalDate date);

    List<Reservation> findByUserIdAndIsDeletedFalse(Long userId);

    List<Reservation> findByAccommodationIdAndIsDeletedFalse(Long accommodationId);

    List<Reservation> findByAccommodationIdAndStatusInAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(Long accommodationId, List<ReservationStatus> statuses, LocalDate checkOut, LocalDate checkIn);

    List<Reservation> findByUserIdAndStatusAndIsDeletedFalse(Long userId, ReservationStatus status);
}