package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Notification;
import edu.uniquindio.stayhub.api.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Notification entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for specific data access needs.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Finds all non-deleted notifications for a user that have a specific status.
     *
     * @param userId The ID of the user.
     * @param status The notification status to filter by.
     * @return A list of Notification entities that match the criteria.
     */
    List<Notification> findByUserIdAndStatusAndDeletedFalse(Long userId, NotificationStatus status);

    /**
     * Finds all non-deleted notifications for a specific user, regardless of their status.
     *
     * @param userId The ID of the user.
     * @return A list of Notification entities for the user.
     */
    List<Notification> findByUserIdAndDeletedFalse(Long userId);
}