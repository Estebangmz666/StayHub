package edu.uniquindio.stayhub.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for successful operation response")
public record SuccessResponseDTO(
        @Schema(description = "The response message", example = "The operation was successful") String message) {
}