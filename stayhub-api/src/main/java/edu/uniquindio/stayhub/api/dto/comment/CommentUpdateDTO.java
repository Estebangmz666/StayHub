package edu.uniquindio.stayhub.api.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentUpdateDTO {
    @NotBlank(message = "Text is required")
    @Size(max = 500, message = "Text cannot exceed 500 characters")
    private String text;

    @NotNull(message = "Rating is required")
    private Integer rating;
}