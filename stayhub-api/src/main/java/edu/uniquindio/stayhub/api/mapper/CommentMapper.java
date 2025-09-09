package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Comment toEntity(CommentRequestDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Comment toEntity(CommentUpdateDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "createdAt", target = "createdAt")
    CommentResponseDTO toResponseDto(Comment comment);
}