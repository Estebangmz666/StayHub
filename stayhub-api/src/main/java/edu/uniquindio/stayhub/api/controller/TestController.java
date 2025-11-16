package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.test.TestResponseDTO;
import edu.uniquindio.stayhub.api.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "Endpoints to check API and controller health")
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    private final HealthService healthService;

    @Operation(summary = "Check real-time health status of all controllers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Health check completed (some services may be DOWN)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TestResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "All UP",
                                    value = """
                                            {
                                                "accommodationControllerStatus": "UP",
                                                "amenityControllerStatus": "UP",
                                                "commentControllerStatus": "UP",
                                                "reservationControllerStatus": "UP",
                                                "userControllerStatus": "UP",
                                                "timestamp": "2025-11-15T18:57:30.123"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Critical failure: multiple controllers DOWN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TestResponseDTO.class)
                    )
            )
    })
    @GetMapping("/health")
    public ResponseEntity<TestResponseDTO> healthCheck() {
        LOGGER.info("Health check requested for all controllers");
        TestResponseDTO health = healthService.checkAllControllers();

        long downCount = health.getDownCount();
        HttpStatus status = downCount == 0 ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;

        LOGGER.warn("Health check completed. {} controller(s) DOWN.", downCount);
        return new ResponseEntity<>(health, status);
    }

    @Operation(summary = "Simple ping for load balancers")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("PONG from TestController");
    }
}