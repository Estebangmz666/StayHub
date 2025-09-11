package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between Comment DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Converts a {@link CommentRequestDTO} to a new {@link Comment} entity.
     * <p>
     * The following fields are ignored during the mapping, as they are typically
     * managed by the system or another service layer:
     * <ul>
     * <li>{@code id} (auto-generated)</li>
     * <li>{@code user} (assigned in service layer)</li>
     * <li>{@code accommodation} (assigned in service layer)</li>
     * <li>{@code createdAt} (assigned on creation)</li>
     * <li>{@code deleted} (default value assigned)</li>
     * </ul>
     *
     * @param dto The DTO containing the data for the new comment.
     * @return A new {@link Comment} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment toEntity(CommentRequestDTO dto);

    /**
     * Converts a {@link CommentUpdateDTO} to a new {@link Comment} entity.
     * <p>
     * This method creates a new entity from the update DTO. Fields like {@code id}
     * and timestamps are ignored as they should be managed by the database.
     *
     * @param dto The DTO containing the updated data.
     * @return A new {@link Comment} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment toEntity(CommentUpdateDTO dto);

    /**
     * Converts a {@link Comment} entity to a {@link CommentResponseDTO}.
     * <p>
     * This mapping extracts specific fields from the comment and its related entities,
     * such as the user's ID and the accommodation's ID, to provide a clean response DTO.
     *
     * @param comment The {@link Comment} entity to convert.
     * @return A new {@link CommentResponseDTO}.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "createdAt", target = "createdAt")
    CommentResponseDTO toResponseDto(Comment comment);
}