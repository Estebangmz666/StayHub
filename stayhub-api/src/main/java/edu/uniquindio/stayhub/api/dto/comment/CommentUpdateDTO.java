package edu.uniquindio.stayhub.api.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for updating an existing comment.
 * This DTO contains the fields that can be modified on an existing comment.
 */
@Getter
@Setter
@Schema(description = "DTO for updating an existing comment")
public class CommentUpdateDTO {
    /**
     * The updated text content of the comment.
     */
    @NotBlank(message = "Text is required")
    @Size(max = 500, message = "Text cannot exceed 500 characters")
    @Schema(description = "The updated text content of the comment", example = "The location was great, but the Wi-Fi was a bit slow.")
    private String text;

    /**
     * The updated rating for the accommodation.
     */
    @NotNull(message = "Rating is required")
    @Schema(description = "The updated rating for the accommodation", example = "4")
    private Integer rating;
}