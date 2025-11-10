package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.mapper.AccommodationMapper;
import edu.uniquindio.stayhub.api.model.*;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class for managing accommodation-related operations in the StayHub application.
 * Handles creation, updating, and soft-deletion of accommodations with role-based access control.
 */
@Service
@Transactional
public class AccommodationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationService.class);

    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final AccommodationMapper accommodationMapper;
    private final NotificationService notificationService;

    public AccommodationService(AccommodationRepository accommodationRepository, UserRepository userRepository,
                                AccommodationMapper accommodationMapper, NotificationService notificationService) {
        this.accommodationRepository = accommodationRepository;
        this.userRepository = userRepository;
        this.accommodationMapper = accommodationMapper;
        this.notificationService = notificationService;
    }

    /**
     * Creates new accommodation for the specified host.
     *
     * @param requestDTO The accommodation creation details.
     * @param username The username (email) of the authenticated user.
     * @return The created accommodation details.
     * @throws AccessDeniedException If the user is not a host.
     */
    public AccommodationResponseDTO createAccommodation(AccommodationRequestDTO requestDTO, String username) throws MessagingException {
        LOGGER.info("Creating accommodation for user: {}", username);
        User user = getUserByEmail(username);
        if (user.getRole() != Role.HOST) {
            LOGGER.error("User {} is not a host", username);
            throw new AccessDeniedException("Solo los anfitriones pueden crear alojamientos");
        }
        Accommodation accommodation = accommodationMapper.toEntity(requestDTO);
        accommodation.setHost(user);
        accommodation.setDeleted(false);
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        LOGGER.debug("Accommodation created with ID: {}", savedAccommodation.getId());

        // Send notification to host
        notificationService.createNotification(new NotificationRequestDTO(
                user.getId(),
                NotificationType.ACCOMMODATION_CREATED,
                "Your accommodation " + accommodation.getTitle() + " has been created successfully!",
                NotificationStatus.UNREAD
        ));

        return accommodationMapper.toResponseDTO(savedAccommodation);
    }

    /**
     * Updates an existing accommodation.
     *
     * @param accommodationId The ID of the accommodation to update.
     * @param updateDTO The updated accommodation details.
     * @param username The username (email) of the authenticated user.
     * @return The updated accommodation details.
     * @throws AccommodationNotFoundException If the accommodation does not exist.
     * @throws AccessDeniedException If the user is not the owner or not a host.
     */
    public AccommodationResponseDTO updateAccommodation(Long accommodationId, AccommodationUpdateDTO updateDTO, String username) throws MessagingException {
        LOGGER.info("Updating accommodation ID: {} for user: {}", accommodationId, username);
        User user = getUserByEmail(username);
        Accommodation accommodation = getAccommodationById(accommodationId);
        validateHostAndOwnership(user, accommodation, "update");
        accommodationMapper.updateEntity(updateDTO, accommodation);
        Accommodation updatedAccommodation = accommodationRepository.save(accommodation);
        LOGGER.debug("Accommodation ID: {} updated", accommodationId);

        notificationService.createNotification(new NotificationRequestDTO(
                user.getId(),
                NotificationType.ACCOMMODATION_UPDATED,
                "Your accommodation " + accommodation.getTitle() + " has been updated successfully!",
                NotificationStatus.UNREAD
        ));
        return accommodationMapper.toResponseDTO(updatedAccommodation);
    }

    /**
     * Soft-deletes accommodation by setting its deleted flag to true.
     *
     * @param accommodationId The ID of the accommodation to delete.
     * @param username The username (email) of the authenticated user.
     * @throws AccommodationNotFoundException If the accommodation does not exist.
     * @throws AccessDeniedException If the user is not the owner or not a host.
     */
    public void deleteAccommodation(Long accommodationId, String username) throws MessagingException {
        LOGGER.info("Deleting accommodation ID: {} for user: {}", accommodationId, username);
        User user = getUserByEmail(username);
        Accommodation accommodation = getAccommodationById(accommodationId);
        validateHostAndOwnership(user, accommodation, "delete");
        accommodation.setDeleted(true);
        accommodationRepository.save(accommodation);
        LOGGER.debug("Accommodation ID: {} deleted", accommodationId);

        notificationService.createNotification(new NotificationRequestDTO(
                user.getId(),
                NotificationType.ACCOMMODATION_DELETED,
                "Your accommodation " + accommodation.getTitle() + " has been deleted successfully!",
                NotificationStatus.UNREAD
        ));
    }

    /**
     * Retrieves an accommodation entity by its ID, ensuring it is not soft-deleted.
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
                    return new AccommodationNotFoundException("El alojamiento no existe");
                });
    }

    /**
     * Retrieves a user by their email address.
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
     * Validates that the authenticated user is a host and is the owner of the accommodation.
     *
     * @param user The authenticated user to validate.
     * @param accommodation The accommodation to check ownership for.
     * @param action The action being performed (for generating a descriptive error message).
     * @throws AccessDeniedException If the user is not a host or does not own the accommodation.
     */
    private void validateHostAndOwnership(User user, Accommodation accommodation, String action) {
        if (user.getRole() != Role.HOST || !accommodation.getHost().getId().equals(user.getId())) {
            LOGGER.error("User {} does not have permission to {} accommodation ID: {}", user.getEmail(), action, accommodation.getId());
            throw new AccessDeniedException("No tienes permiso para " + action + " este alojamiento");
        }
    }

    public Page<AccommodationResponseDTO> listAccommodations(Pageable pageable) {
        LOGGER.info("Fetching all active accommodations with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Accommodation> accommodations = accommodationRepository.findAllByDeletedFalse(pageable);

        LOGGER.debug("Retrieved {} accommodations from database", accommodations.getTotalElements());

        return accommodations.map(accommodationMapper::toResponseDTO);
    }

    public AccommodationResponseDTO getAccommodation(Long id) {
        LOGGER.info("Fetching accommodation with ID: {}", id);

        Accommodation accommodation = accommodationRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> {
                    LOGGER.warn("Accommodation not found with ID: {}", id);
                    return new AccommodationNotFoundException("El alojamiento no existe");
                });

        LOGGER.debug("Accommodation retrieved successfully with ID: {}", id);
        return accommodationMapper.toResponseDTO(accommodation);
    }

    public Page<AccommodationResponseDTO> searchAccommodations(
            String city,
            @Positive Integer minCapacity,
            @Positive BigDecimal maxPrice,
            List<Long> amenityIds,
            Pageable pageable) {

        LOGGER.info("Searching accommodations with filters: city={}, minCapacity={}, maxPrice={}, amenityIds={}",
                city, minCapacity, maxPrice, amenityIds);

        if (amenityIds != null && amenityIds.contains(null)) {
            throw new IllegalArgumentException("Las listas de IDs de amenidades no pueden contener valores nulos");
        }

        List<Long> safeAmenityIds = amenityIds == null ? Collections.emptyList() : new ArrayList<>(amenityIds);

        Page<Accommodation> result = accommodationRepository.findByFilters(
                city,
                minCapacity,
                maxPrice,
                safeAmenityIds.isEmpty() ? null : safeAmenityIds, // pasar null si está vacía, según cómo lo manejes en el query
                pageable
        );

        LOGGER.debug("Search returned {} total accommodations", result.getTotalElements());

        return result.map(accommodationMapper::toResponseDTO);
    }

    public Page<AccommodationResponseDTO> getAccommodationsByHost(String username, Pageable pageable) {
        User host = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Page<Accommodation> accommodations = accommodationRepository
                .findByHostAndDeletedFalse(host, pageable);

        return accommodations.map(accommodationMapper::toResponseDTO);
    }
}