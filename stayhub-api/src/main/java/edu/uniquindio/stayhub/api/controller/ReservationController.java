package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reservation Management", description = "Endpoints for creating, updating, and canceling reservations")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Create a new reservation", description = "Allows a guest to book an accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"status\": \"CONFIRMED\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Las fechas seleccionadas no están disponibles\", \"code\": 400}")))
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody @Schema(description = "Reservation creation details") ReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.createReservation(requestDTO);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update a reservation status", description = "Allows a guest to update or cancel a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"status\": \"CANCELLED\"}"))),
            @ApiResponse(responseCode = "404", description = "Reservation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"La reserva no existe\", \"code\": 404}")))
    })
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable @Parameter(description = "Reservation ID") Long reservationId,
            @RequestBody @Schema(description = "Reservation update details") ReservationUpdateDTO updateDTO) {
        ReservationResponseDTO response = reservationService.updateReservation(reservationId, updateDTO);
        return ResponseEntity.ok(response);
    }
}