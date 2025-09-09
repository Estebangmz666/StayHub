package edu.uniquindio.stayhub.api.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CommentResponseDTO {
    private Long id;
    private Long userId;
    private Long accommodationId;
    private String text;
    private Integer rating;
    private LocalDateTime createdAt;
}