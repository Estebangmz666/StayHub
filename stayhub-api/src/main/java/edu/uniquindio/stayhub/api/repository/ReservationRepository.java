package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Reservation entities.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Finds all non-deleted reservations for a guest.
     *
     * @param guestId The ID of the guest.
     * @return A list of reservations.
     */
    List<Reservation> findByGuestIdAndDeletedFalse(Long guestId);

    /**
     * Finds all non-deleted reservations for a guest with a specific status.
     *
     * @param guestId The ID of the guest.
     * @param status The reservation status.
     * @return A list of reservations.
     */
    List<Reservation> findByGuestIdAndStatusAndDeletedFalse(Long guestId, ReservationStatus status);

    /**
     * Finds all non-deleted reservations for accommodation.
     *
     * @param accommodationId The ID of the accommodation.
     * @return A list of reservations.
     */
    List<Reservation> findByAccommodationIdAndDeletedFalse(Long accommodationId);

    /**
     * Finds all non-deleted reservations for accommodation with a specific status.
     *
     * @param accommodationId The ID of the accommodation.
     * @param status The reservation status.
     * @return A list of reservations.
     */
    List<Reservation> findByAccommodationIdAndStatusAndDeletedFalse(Long accommodationId, ReservationStatus status);

    /**
     * Checks if accommodation has overlapping reservations for the given dates.
     * The query specifically looks for reservations that are not deleted and are
     * either in PENDING or CONFIRMED status.
     *
     * @param accommodationId The ID of the accommodation.
     * @param checkInDate The check-in date.
     * @param checkOutDate The check-out date.
     * @return True if there are overlapping reservations, false otherwise.
     */
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.accommodation.id = :accommodationId " +
            "AND r.deleted = false " +
            "AND r.status IN ('PENDING', 'CONFIRMED') " +
            "AND (r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate)")
    boolean existsOverlappingReservations(Long accommodationId, LocalDateTime checkInDate, LocalDateTime checkOutDate);

    /**
     * Finds all reservations with a specific check-in date.
     *
     * @param checkInDate The check-in date.
     * @return A list of reservations.
     */
    List<Reservation> findByCheckInDate(LocalDateTime checkInDate);
}