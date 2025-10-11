package edu.uniquindio.stayhub.api.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for creating a new comment.
 * This DTO is used to capture the details of a new comment, including the user, accommodation, and the rating.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for creating a new comment")
public class CommentRequestDTO {
    /**
     * The ID of the user creating the comment.
     */
    @NotNull(message = "User ID is required")
    @Schema(description = "The ID of the user creating the comment", example = "1")
    private Long userId;

    /**
     * The ID of the accommodation the comment is for.
     */
    @NotNull(message = "Accommodation ID is required")
    @Schema(description = "The ID of the accommodation the comment is for", example = "101")
    private Long accommodationId;

    /**
     * The text content of the comment.
     */
    @NotBlank(message = "Text is required")
    @Size(max = 500, message = "Text cannot exceed 500 characters")
    @Schema(description = "The text content of the comment", example = "This was a wonderful stay, very clean and comfortable!")
    private String text;

    /**
     * The rating given to the accommodation.
     */
    @NotNull(message = "Rating is required")
    @Schema(description = "The rating given to the accommodation", example = "5")
    private Integer rating;
}