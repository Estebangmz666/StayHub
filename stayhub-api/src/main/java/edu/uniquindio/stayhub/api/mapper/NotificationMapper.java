package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationResponseDTO;
import edu.uniquindio.stayhub.api.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Notification DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    /**
     * Converts a {@link NotificationRequestDTO} to a new {@link Notification} entity.
     * <p>
     * This mapping handles the conversion of the {@code notificationType} to the entity's {@code type}
     * field. Other fields are ignored as they are managed by the system.
     *
     * @param dto The DTO to convert.
     * @return A new {@link Notification} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deleted", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "notificationType", target = "type")
    Notification toEntity(NotificationRequestDTO dto);

    /**
     * Converts a {@link Notification} entity to a {@link NotificationResponseDTO}.
     * <p>
     * This mapping extracts the user's ID and re-maps the {@code type} field back
     * to {@code notificationType} for the response DTO.
     *
     * @param notification The entity to convert.
     * @return A new {@link NotificationResponseDTO}.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "type", target = "notificationType")
    NotificationResponseDTO toResponseDTO(Notification notification);
}