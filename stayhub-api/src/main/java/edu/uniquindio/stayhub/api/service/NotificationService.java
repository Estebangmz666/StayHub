package edu.uniquindio.stayhub.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationResponseDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.NotificationNotFoundException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.mapper.NotificationMapper;
import edu.uniquindio.stayhub.api.model.Notification;
import edu.uniquindio.stayhub.api.model.NotificationStatus;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.NotificationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

/**
 * Service class for managing notification-related operations in the StayHub application.
 * Handles creation, retrieval, updating, and soft-deletion of notifications with role-based access control.
 */
@Service
@Transactional
@Validated
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    /**
     * Creates a new notification for the specified user.
     *
     * @param requestDTO The notification creation details.
     * @throws UserNotFoundException If the user does not exist.
     */
    public void createNotification(@Valid NotificationRequestDTO requestDTO) {
        LOGGER.info("Creating notification for user ID: {}", requestDTO.getUserId());
        User user = getUserById(requestDTO.getUserId());
        Notification notification = notificationMapper.toEntity(requestDTO);
        notification.setUser(user);
        notification.setDeleted(false);
        Notification savedNotification = notificationRepository.save(notification);
        LOGGER.debug("Notification created with ID: {}", savedNotification.getId());
        notificationMapper.toResponseDTO(savedNotification);
    }

    /**
     * Retrieves notifications for a user, optionally filtered by status.
     *
     * @param userId The ID of the user.
     * @param username The username (email) of the authenticated user.
     * @param status The notification status to filter by (optional).
     * @return A list of notification details.
     * @throws UserNotFoundException If the user does not exist.
     * @throws AccessDeniedException If the authenticated user does not have permission.
     */
    public List<NotificationResponseDTO> getNotificationsByUser(Long userId, String username, String status) {
        LOGGER.info("Retrieving notifications for user ID: {}, requested by: {}", userId, username);
        User user = getUserById(userId);
        User authenticatedUser = getUserByEmail(username);
        if (!authenticatedUser.getId().equals(userId)) {
            LOGGER.error("User {} does not have permission to view notifications for user ID: {}", username, userId);
            throw new AccessDeniedException("No tienes permiso para ver las notificaciones de este usuario");
        }
        List<Notification> notifications;
        if (status != null && !status.isEmpty()) {
            try {
                NotificationStatus notificationStatus = NotificationStatus.valueOf(status.toUpperCase());
                notifications = notificationRepository.findByUserIdAndStatusAndDeletedFalse(userId, notificationStatus);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid notification status: {}", status);
                throw new IllegalArgumentException("Estado de notificación inválido: " + status);
            }
        } else {
            notifications = notificationRepository.findByUserIdAndDeletedFalse(userId);
        }
        return notifications.stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a notification.
     *
     * @param notificationId The ID of the notification to update.
     * @param updateDTO The updated notification status.
     * @param username The username (email) of the authenticated user.
     * @return The updated notification details.
     * @throws NotificationNotFoundException If the notification does not exist.
     * @throws AccessDeniedException If the user does not have permission.
     */
    public NotificationResponseDTO updateNotificationStatus(Long notificationId, @Valid NotificationUpdateDTO updateDTO, String username) {
        LOGGER.info("Updating notification ID: {} for user: {}", notificationId, username);
        Notification notification = getNotificationById(notificationId);
        User authenticatedUser = getUserByEmail(username);
        validateUserOwnership(authenticatedUser, notification, "update");
        notification.setStatus(updateDTO.getStatus());
        Notification updatedNotification = notificationRepository.save(notification);
        LOGGER.debug("Notification ID: {} updated", notificationId);
        return notificationMapper.toResponseDTO(updatedNotification);
    }

    /**
     * Soft-deletes a notification.
     *
     * @param notificationId The ID of the notification to delete.
     * @param username The username (email) of the authenticated user.
     * @throws NotificationNotFoundException If the notification does not exist.
     * @throws AccessDeniedException If the user does not have permission.
     */
    public void deleteNotification(Long notificationId, String username) {
        LOGGER.info("Deleting notification ID: {} for user: {}", notificationId, username);
        Notification notification = getNotificationById(notificationId);
        User authenticatedUser = getUserByEmail(username);
        validateUserOwnership(authenticatedUser, notification, "delete");
        notification.setDeleted(true);
        notificationRepository.save(notification);
        LOGGER.debug("Notification ID: {} deleted", notificationId);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId The ID of the user.
     * @return The user entity.
     * @throws UserNotFoundException If the user does not exist.
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    LOGGER.error("User ID {} not found", userId);
                    return new UserNotFoundException("Usuario con ID " + userId + " no encontrado");
                });
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
     * Retrieves a notification by ID, ensuring it is not deleted.
     *
     * @param notificationId The ID of the notification.
     * @return The notification entity.
     * @throws NotificationNotFoundException If the notification does not exist or is deleted.
     */
    private Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .filter(n -> !n.isDeleted())
                .orElseThrow(() -> {
                    LOGGER.error("Notification ID {} not found or is deleted", notificationId);
                    return new NotificationNotFoundException("Notificación con ID " + notificationId + " no encontrada");
                });
    }

    /**
     * Validates that the user owns the notification.
     *
     * @param user The user to validate.
     * @param notification The notification to check ownership for.
     * @param action The action being performed (for error message).
     * @throws AccessDeniedException If the user does not own the notification.
     */
    private void validateUserOwnership(User user, Notification notification, String action) {
        if (!notification.getUser().getId().equals(user.getId())) {
            LOGGER.error("User {} does not have permission to {} notification ID: {}", user.getEmail(), action, notification.getId());
            throw new AccessDeniedException("No tienes permiso para " + action + " esta notificación");
        }
    }
}