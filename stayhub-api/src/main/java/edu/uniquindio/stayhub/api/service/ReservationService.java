package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub.api.exception.InvalidReservationDatesException;
import edu.uniquindio.stayhub.api.exception.ReservationNotFoundException;
import edu.uniquindio.stayhub.api.mapper.ReservationMapper;
import edu.uniquindio.stayhub.api.model.*;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

/**
 * Service class for managing reservation-related operations in the StayHub application.
 */
@Service
@Transactional
@Validated
public class ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, AccommodationRepository accommodationRepository,
                              UserRepository userRepository, NotificationService notificationService,
                              ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.accommodationRepository = accommodationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.reservationMapper = reservationMapper;
    }

    /**
     * Creates a new reservation for a guest.
     *
     * @param requestDTO The reservation creation details.
     * @param username The username (email) of the authenticated user.
     * @return The created reservation details.
     * @throws AccessDeniedException If the user is not a guest.
     * @throws AccommodationNotFoundException If the accommodation does not exist.
     * @throws InvalidReservationDatesException If the dates are invalid or unavailable.
     */
    public ReservationResponseDTO createReservation(@Valid ReservationRequestDTO requestDTO, String username) throws MessagingException {
        LOGGER.info("Creating reservation for user: {}", username);
        User guest = getUserByEmail(username);
        if (guest.getRole() != Role.GUEST) {
            LOGGER.error("User {} is not a guest", username);
            throw new AccessDeniedException("Solo los huéspedes pueden crear reservas");
        }
        if (!guest.getId().equals(requestDTO.getGuestId())) {
            LOGGER.error("User {} cannot create reservation for guest ID: {}", username, requestDTO.getGuestId());
            throw new AccessDeniedException("No puedes crear reservas para otro usuario");
        }
        Accommodation accommodation = getAccommodationById(requestDTO.getAccommodationId());
        validateReservationDates(requestDTO, accommodation);
        Reservation reservation = reservationMapper.toEntity(requestDTO);
        reservation.setGuest(guest);
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(calculateTotalPrice(requestDTO, accommodation));
        reservation.setDeleted(false);
        Reservation savedReservation = reservationRepository.save(reservation);
        LOGGER.debug("Reservation created with ID: {}", savedReservation.getId());

        notificationService.createNotification(new NotificationRequestDTO(
                guest.getId(),
                NotificationType.RESERVATION_CREATED,
                "Tu reserva para " + accommodation.getTitle() + " ha sido creada exitosamente!",
                NotificationStatus.UNREAD
        ));

        notificationService.createNotification(new NotificationRequestDTO(
                accommodation.getHost().getId(),
                NotificationType.RESERVATION_REQUESTED,
                "Se ha recibido una nueva solicitud de reserva para " + accommodation.getTitle() + "!",
                NotificationStatus.UNREAD
        ));

        return reservationMapper.toResponseDTO(savedReservation);
    }

    /**
     * Updates the status of a reservation.
     *
     * @param reservationId The ID of the reservation to update.
     * @param updateDTO The updated reservation status.
     * @param username The username (email) of the authenticated user.
     * @return The updated reservation details.
     * @throws ReservationNotFoundException If the reservation does not exist.
     * @throws AccessDeniedException If the user does not have permission.
     */
    public ReservationResponseDTO updateReservation(Long reservationId, @Valid ReservationUpdateDTO updateDTO, String username) throws MessagingException {
        LOGGER.info("Updating reservation ID: {} for user: {}", reservationId, username);
        Reservation reservation = getReservationByIdInternal(reservationId);
        User authenticatedUser = getUserByEmail(username);
        validateUserPermission(authenticatedUser, reservation, "actualizar");
        reservationMapper.updateEntity(updateDTO, reservation);
        Reservation updatedReservation = reservationRepository.save(reservation);
        LOGGER.debug("Reservation ID: {} updated", reservationId);

        notificationService.createNotification(new NotificationRequestDTO(
                reservation.getGuest().getId(),
                NotificationType.RESERVATION_UPDATED,
                "Tu reserva para " + reservation.getAccommodation().getTitle() + " ha sido actualizada a " + updateDTO.getStatus() + "!",
                NotificationStatus.UNREAD
        ));

        notificationService.createNotification(new NotificationRequestDTO(
                reservation.getAccommodation().getHost().getId(),
                NotificationType.RESERVATION_UPDATED,
                "La reserva para " + reservation.getAccommodation().getTitle() + " ha sido actualizada a " + updateDTO.getStatus() + "!",
                NotificationStatus.UNREAD
        ));

        return reservationMapper.toResponseDTO(updatedReservation);
    }

    /**
     * Cancels a reservation (soft delete).
     *
     * @param reservationId The ID of the reservation to cancel.
     * @param username The username (email) of the authenticated user.
     * @throws ReservationNotFoundException If the reservation does not exist.
     * @throws AccessDeniedException If the user does not have permission.
     */
    public void cancelReservation(Long reservationId, String username) throws MessagingException {
        LOGGER.info("Cancelling reservation ID: {} for user: {}", reservationId, username);
        Reservation reservation = getReservationByIdInternal(reservationId);
        User authenticatedUser = getUserByEmail(username);
        validateUserPermission(authenticatedUser, reservation, "cancelar");
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setDeleted(true);
        reservationRepository.save(reservation);
        LOGGER.debug("Reservation ID: {} cancelled", reservationId);

        notificationService.createNotification(new NotificationRequestDTO(
                reservation.getGuest().getId(),
                NotificationType.RESERVATION_CANCELLED,
                "Tu reserva para " + reservation.getAccommodation().getTitle() + " ha sido cancelada!",
                NotificationStatus.UNREAD
        ));

        notificationService.createNotification(new NotificationRequestDTO(
                reservation.getAccommodation().getHost().getId(),
                NotificationType.RESERVATION_CANCELLED,
                "La reserva para " + reservation.getAccommodation().getTitle() + " ha sido cancelada!",
                NotificationStatus.UNREAD
        ));
    }

    public Page<ReservationResponseDTO> getReservationsByGuest(
            String username,
            String status,
            Pageable pageable) {
        LOGGER.info("Fetching reservations for guest: {}, status: {}, page: {}", username, status, pageable.getPageNumber());

        User user = getUserByEmail(username);

        Page<Reservation> reservations;

        if (status != null && !status.isEmpty()) {
            try {
                ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
                reservations = reservationRepository.findByGuestIdAndStatusAndDeletedFalse(
                        user.getId(),
                        reservationStatus,
                        pageable
                );
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid reservation status: {}", status);
                throw new IllegalArgumentException("Estado de reserva inválido: " + status);
            }
        } else {
            reservations = reservationRepository.findByGuestIdAndDeletedFalse(user.getId(), pageable);
        }

        LOGGER.debug("Retrieved {} reservations for guest: {}", reservations.getTotalElements(), username);
        return reservations.map(reservationMapper::toResponseDTO);
    }

    /**
     * Retrieves reservations for an accommodation, optionally filtered by status.
     *
     * @param accommodationId The ID of the accommodation.
     * @param username The username (email) of the authenticated user.
     * @param status The reservation status to filter by (optional).
     * @return A list of reservation details.
     * @throws AccessDeniedException If the user is not the host of the accommodation.
     */
    public Page<ReservationResponseDTO> getReservationsByAccommodation(
            Long accommodationId,
            String username,
            String status,
            Pageable pageable) {
        LOGGER.info("Fetching reservations for accommodation ID: {}, requested by: {}, status: {}",
                accommodationId, username, status);

        // Validar que el accommodation existe
        Accommodation accommodation = getAccommodationById(accommodationId);

        // Validar que el usuario es el host del accommodation
        User user = getUserByEmail(username);

        if (!accommodation.getHost().getId().equals(user.getId())) {
            LOGGER.error("User {} attempted to access reservations for accommodation they don't own", username);
            throw new AccessDeniedException("No tienes permiso para ver las reservas de este alojamiento");
        }

        Page<Reservation> reservations;

        if (status != null && !status.isEmpty()) {
            try {
                ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
                reservations = reservationRepository.findByAccommodationIdAndStatusAndDeletedFalse(
                        accommodationId,
                        reservationStatus,
                        pageable
                );
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid reservation status: {}", status);
                throw new IllegalArgumentException("Estado de reserva inválido: " + status);
            }
        } else {
            reservations = reservationRepository.findByAccommodationIdAndDeletedFalse(
                    accommodationId,
                    pageable
            );
        }

        LOGGER.debug("Retrieved {} reservations for accommodation ID: {}", reservations.getTotalElements(), accommodationId);
        return reservations.map(reservationMapper::toResponseDTO);
    }

    /**
     * Retrieves a reservation entity by ID (internal use only).
     *
     * @param reservationId The ID of the reservation.
     * @return The reservation entity.
     * @throws ReservationNotFoundException If the reservation does not exist or is deleted.
     */
    private Reservation getReservationByIdInternal(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> {
                    LOGGER.error("Reservation ID {} not found or is deleted", reservationId);
                    return new ReservationNotFoundException("La reserva con ID " + reservationId + " no existe");
                });
    }

    /**
     * Retrieves accommodation by ID.
     *
     * @param accommodationId The ID of the accommodation.
     * @return The accommodation entity.
     * @throws AccommodationNotFoundException If the accommodation does not exist or is deleted.
     */
    private Accommodation getAccommodationById(Long accommodationId) {
        return accommodationRepository.findById(accommodationId)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> {
                    LOGGER.error("Accommodation ID {} not found or is deleted", accommodationId);
                    return new AccommodationNotFoundException("El alojamiento con ID " + accommodationId + " no existe");
                });
    }

    public ReservationResponseDTO getReservationById(Long reservationId, String username) {
        LOGGER.info("Fetching reservation with ID: {} for user: {}", reservationId, username);

        Reservation reservation = reservationRepository.findById(reservationId)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> {
                    LOGGER.error("Reservation ID {} not found or is deleted", reservationId);
                    return new ReservationNotFoundException("La reserva con ID " + reservationId + " no existe");
                });

        User user = getUserByEmail(username);

        // Validar que el usuario sea el guest o el host del accommodation
        boolean isGuest = reservation.getGuest().getId().equals(user.getId());
        boolean isHost = reservation.getAccommodation().getHost().getId().equals(user.getId());

        if (!isGuest && !isHost) {
            LOGGER.error("User {} attempted to access reservation they don't own", username);
            throw new AccessDeniedException("No tienes permiso para ver esta reserva");
        }

        LOGGER.debug("Reservation retrieved successfully with ID: {}", reservationId);
        return reservationMapper.toResponseDTO(reservation);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user.
     * @return The user entity.
     * @throws AccessDeniedException If the user does not exist.
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.error("User {} not found", email);
                    return new AccessDeniedException("Usuario no encontrado");
                });
    }

    /**
     * Validates that the user has permission to modify the reservation.
     *
     * @param user The user to validate.
     * @param reservation The reservation to check permission for.
     * @param action The action being performed (for an error message).
     * @throws AccessDeniedException If the user does not have permission.
     */
    private void validateUserPermission(User user, Reservation reservation, String action) {
        if (!reservation.getGuest().getId().equals(user.getId()) &&
                !reservation.getAccommodation().getHost().getId().equals(user.getId())) {
            LOGGER.error("User {} does not have permission to {} reservation ID: {}", user.getEmail(), action, reservation.getId());
            throw new AccessDeniedException("No tienes permiso para " + action + " esta reserva");
        }
    }

    /**
     * Validates reservation dates and availability.
     *
     * @param requestDTO The reservation request details.
     * @param accommodation The accommodation to validate.
     * @throws InvalidReservationDatesException If the dates are invalid or unavailable.
     */
    private void validateReservationDates(ReservationRequestDTO requestDTO, Accommodation accommodation) {
        if (requestDTO.getCheckInDate().isAfter(requestDTO.getCheckOutDate())) {
            LOGGER.error("Check-in date {} is after check-out date {}", requestDTO.getCheckInDate(), requestDTO.getCheckOutDate());
            throw new InvalidReservationDatesException("La fecha de check-in debe ser antes de la fecha de check-out");
        }
        if (requestDTO.getNumberOfGuests() > accommodation.getCapacity()) {
            LOGGER.error("Guest count {} exceeds accommodation capacity {}", requestDTO.getNumberOfGuests(), accommodation.getCapacity());
            throw new InvalidReservationDatesException("El número de huéspedes excede la capacidad del alojamiento");
        }
        if (reservationRepository.existsOverlappingReservations(
                requestDTO.getAccommodationId(),
                requestDTO.getCheckInDate(),
                requestDTO.getCheckOutDate())) {
            LOGGER.error("Accommodation ID {} is not available for dates {} to {}", requestDTO.getAccommodationId(),
                    requestDTO.getCheckInDate(), requestDTO.getCheckOutDate());
            throw new InvalidReservationDatesException("Las fechas seleccionadas no están disponibles");
        }
    }

    /**
     * Calculates the total price of a reservation based on the number of nights and accommodation price.
     *
     * @param requestDTO The reservation request details.
     * @param accommodation The accommodation.
     * @return The total price.
     */
    private BigDecimal calculateTotalPrice(ReservationRequestDTO requestDTO, Accommodation accommodation) {
        long nights = ChronoUnit.DAYS.between(
                requestDTO.getCheckInDate().toLocalDate(),
                requestDTO.getCheckOutDate().toLocalDate());
        return accommodation.getPricePerNight().multiply(BigDecimal.valueOf(nights));
    }
}