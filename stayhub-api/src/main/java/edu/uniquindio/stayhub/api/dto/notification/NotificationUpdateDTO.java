package edu.uniquindio.stayhub.api.dto.notification;

import edu.uniquindio.stayhub.api.model.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for updating an existing notification.
 * It is used to change the status of a notification, for example, from `SENT` to `READ`.
 */
@Getter @Setter @NotNull @AllArgsConstructor @Schema(description = "DTO for updating an existing notification's status")
public class NotificationUpdateDTO {

    /**
     * The updated status for the notification (e.g., READ).
     */
    @NotNull(message = "Status is required")
    @Schema(description = "The updated status for the notification", example = "READ")
    private NotificationStatus status;
}