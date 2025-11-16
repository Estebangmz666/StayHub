package edu.uniquindio.stayhub.api.dto.test;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing the health status of API controllers")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestResponseDTO {

    @Schema(description = "Status of accommodation controller")
    String accommodationControllerStatus;

    @Schema(description = "Status of amenity controller")
    String amenityControllerStatus;

    @Schema(description = "Status of reservation controller")
    String commentControllerStatus;

    @Schema(description = "Status of reservation controller")
    String reservationControllerStatus;

    @Schema(description = "Status of user controller")
    String userControllerStatus;

    @Schema(description = "The timestamp of the health check")
    LocalDateTime timestamp;

    public long getDownCount() {
        return java.util.stream.Stream.of(
                accommodationControllerStatus,
                amenityControllerStatus,
                commentControllerStatus,
                reservationControllerStatus,
                userControllerStatus
        ).filter(s -> s == null || !s.startsWith("UP")).count();
    }
}