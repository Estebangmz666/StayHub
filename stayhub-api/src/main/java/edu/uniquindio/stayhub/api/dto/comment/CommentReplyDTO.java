package edu.uniquindio.stayhub.api.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class CommentReplyDTO {
    @NotBlank(message = "The reply cannot be empty")
    @Size(max = 500, message = "The reply text cannot excede 500 characters")
    private String replyText;
}