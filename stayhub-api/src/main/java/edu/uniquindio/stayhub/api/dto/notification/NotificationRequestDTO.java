package edu.uniquindio.stayhub.api.dto.notification;

import edu.uniquindio.stayhub.api.model.NotificationType;
import edu.uniquindio.stayhub.api.model.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for creating a new notification.
 * It contains all the necessary details to send a notification to a user.
 */
@Getter
@Setter
@Schema(description = "DTO for creating a new notification")
public class NotificationRequestDTO {

    /**
     * The ID of the user who will receive the notification.
     */
    @NotNull(message = "User ID is required")
    @Schema(description = "The ID of the user who will receive the notification", example = "10")
    private Long userId;

    /**
     * The type of the notification (e.g., BOOKING, PROMOTION).
     */
    @NotNull(message = "Notification type is required")
    @Schema(description = "The type of the notification", example = "BOOKING")
    private NotificationType notificationType;

    /**
     * The message content of the notification.
     */
    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    @Schema(description = "The message content of the notification", example = "Your booking for 'House in El Poblado' has been confirmed.")
    private String message;

    /**
     * The initial status of the notification (e.g., SENT, READ).
     */
    @NotNull(message = "Status is required")
    @Schema(description = "The initial status of the notification", example = "SENT")
    private NotificationStatus status;

    public NotificationRequestDTO(Long userId, NotificationType type, String message, NotificationStatus status) {
        this.userId = userId;
        this.notificationType = type;
        this.message = message;
        this.status = status;
    }
}