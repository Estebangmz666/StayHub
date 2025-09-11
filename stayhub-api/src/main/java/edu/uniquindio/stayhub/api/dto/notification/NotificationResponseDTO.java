package edu.uniquindio.stayhub.api.dto.notification;

import edu.uniquindio.stayhub.api.model.NotificationType;
import edu.uniquindio.stayhub.api.model.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning notification details.
 * It provides a complete representation of a notification, including its metadata.
 */
@Getter
@Setter
@Schema(description = "DTO for returning notification details to the client")
public class NotificationResponseDTO {

    /**
     * The unique identifier of the notification.
     */
    @Schema(description = "The unique identifier of the notification", example = "1")
    private Long id;

    /**
     * The ID of the user who received the notification.
     */
    @Schema(description = "The ID of the user who received the notification", example = "10")
    private Long userId;

    /**
     * The type of the notification (e.g., BOOKING, PROMOTION).
     */
    @Schema(description = "The type of the notification", example = "BOOKING")
    private NotificationType notificationType;

    /**
     * The message content of the notification.
     */
    @Schema(description = "The message content of the notification", example = "Your booking for 'House in El Poblado' has been confirmed.")
    private String message;

    /**
     * The current status of the notification (e.g., SENT, READ).
     */
    @Schema(description = "The current status of the notification", example = "SENT")
    private NotificationStatus status;

    /**
     * The date and time when the notification was created.
     */
    @Schema(description = "The date and time when the notification was created", example = "2025-09-11T14:30:00")
    private LocalDateTime createdAt;

    /**
     * The date and time when the notification was last updated.
     */
    @Schema(description = "The date and time when the notification was last updated", example = "2025-09-11T14:30:00")
    private LocalDateTime updatedAt;
}