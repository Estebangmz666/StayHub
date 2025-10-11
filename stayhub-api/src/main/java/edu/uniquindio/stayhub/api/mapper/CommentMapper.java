package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between Comment DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Converts a {@link CommentRequestDTO} to a new {@link Comment} entity.
     * Ignores fields that are managed by the system or database.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment toEntity(CommentRequestDTO dto);

    /**
     * Converts a {@link CommentUpdateDTO} to a new {@link Comment} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Comment toEntity(CommentUpdateDTO dto);

    /**
     * Updates an existing {@link Comment} entity with data from a DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(CommentUpdateDTO dto, @MappingTarget Comment comment);

    /**
     * Converts a {@link Comment} entity to a {@link CommentResponseDTO}.
     */
    default CommentResponseDTO toResponseDto(Comment comment) {
        if (comment == null) return null;

        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setAccommodationId(comment.getAccommodation().getId());
        dto.setText(comment.getText());
        dto.setRating(comment.getRating());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}