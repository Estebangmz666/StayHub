package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.test.TestResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final edu.uniquindio.stayhub.api.controller.UserController userController;
    private final edu.uniquindio.stayhub.api.controller.AccommodationController accommodationController;
    private final edu.uniquindio.stayhub.api.controller.AmenityController amenityController;
    private final edu.uniquindio.stayhub.api.controller.CommentController commentController;
    private final edu.uniquindio.stayhub.api.controller.ReservationController reservationController;

    public TestResponseDTO checkAllControllers() {
        LocalDateTime now = LocalDateTime.now();
        return TestResponseDTO.builder()
                .accommodationControllerStatus(pingController(accommodationController))
                .amenityControllerStatus(pingController(amenityController))
                .commentControllerStatus(pingController(commentController))
                .reservationControllerStatus(pingController(reservationController))
                .userControllerStatus(pingController(userController))
                .timestamp(now)
                .build();
    }

    private String pingController(Object controller) {
        try {
            if (controller != null) {
                return "UP";
            } else {
                return "DOWN (null)";
            }
        } catch (Exception e) {
            return "DOWN (" + e.getClass().getSimpleName() + ")";
        }
    }
}