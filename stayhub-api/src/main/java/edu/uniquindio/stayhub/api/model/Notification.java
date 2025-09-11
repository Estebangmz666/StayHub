package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a notification in the StayHub application.
 * This class maps to the 'notifications' table and is used to store
 * and manage messages sent to users.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    /**
     * The unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user to whom this notification is sent.
     * This establishes a many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private User user;

    /**
     * The type of the notification, defined by the {@link NotificationType} enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El tipo de notificación es obligatorio")
    private NotificationType type;

    /**
     * The content of the notification message.
     */
    @Column(nullable = false)
    @NotBlank(message = "El mensaje de la notificación es obligatorio")
    private String message;

    /**
     * The status of the notification (e.g., READ, UNREAD), defined by the {@link NotificationStatus} enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado de la notificación es obligatorio")
    private NotificationStatus status;

    /**
     * A flag to indicate if the notification has been soft-deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    /**
     * The timestamp when the notification was created.
     * This field is automatically populated on creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the notification was last updated.
     * This field is automatically updated on each modification.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}