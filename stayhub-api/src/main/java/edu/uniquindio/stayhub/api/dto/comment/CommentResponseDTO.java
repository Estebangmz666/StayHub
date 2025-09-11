package edu.uniquindio.stayhub.api.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning comment details.
 * This DTO is used to provide a full representation of a comment, including its creation date.
 */
@Getter
@Setter
@Schema(description = "DTO for returning comment details")
public class CommentResponseDTO {
    /**
     * The unique identifier of the comment.
     */
    @Schema(description = "The unique identifier of the comment", example = "50")
    private Long id;

    /**
     * The ID of the user who made the comment.
     */
    @Schema(description = "The ID of the user who made the comment", example = "1")
    private Long userId;

    /**
     * The ID of the accommodation the comment belongs to.
     */
    @Schema(description = "The ID of the accommodation the comment belongs to", example = "101")
    private Long accommodationId;

    /**
     * The text content of the comment.
     */
    @Schema(description = "The text content of the comment", example = "This was a wonderful stay, very clean and comfortable!")
    private String text;

    /**
     * The rating given to the accommodation.
     */
    @Schema(description = "The rating given to the accommodation", example = "5")
    private Integer rating;

    /**
     * The date and time when the comment was created.
     */
    @Schema(description = "The date and time when the comment was created", example = "2024-05-15T10:30:00")
    private LocalDateTime createdAt;
}