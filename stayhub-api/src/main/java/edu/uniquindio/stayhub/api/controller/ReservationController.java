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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing reservations in the StayHub application.
 * This class provides endpoints for guests to create, update, cancel, and retrieve
 * their reservations. It also allows hosts to view reservations for their accommodations.
 */
@Tag(name = "Reservation Management", description = "Endpoints for creating, updating, canceling, and retrieving reservations")
@RestController
@RequestMapping("/api/v1/reservations")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Constructs a {@code ReservationController} with the specified service.
     *
     * @param reservationService The service that handles the business logic for reservations.
     */
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Creates a new reservation for an authenticated guest.
     *
     * @param requestDTO     The reservation creation details.
     * @param authentication The authenticated user's details, automatically injected by Spring Security.
     * @return A {@link ResponseEntity} containing the details of the newly created reservation.
     */
    @Operation(summary = "Create a new reservation", description = "Allows an authenticated guest to book an accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"guestId\": 1, \"accommodationId\": 1, \"checkInDate\": \"2025-10-01T14:00:00\", \"checkOutDate\": \"2025-10-03T12:00:00\", \"numberOfGuests\": 2, \"totalPrice\": 200.00, \"status\": \"PENDING\", \"createdAt\": \"2025-09-10T22:00:00\", \"updatedAt\": \"2025-09-10T22:00:00\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or dates not available",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"The selected dates are not available\", \"code\": 400}"))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Only guests can create reservations\", \"code\": 403}")))
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody @Schema(description = "Details for creating a reservation") ReservationRequestDTO requestDTO,
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        ReservationResponseDTO response = reservationService.createReservation(requestDTO, username);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Updates the status of an existing reservation.
     *
     * @param reservationId  The ID of the reservation to update.
     * @param updateDTO      The updated reservation status details.
     * @param authentication The authenticated user's details.
     * @return A {@link ResponseEntity} containing the details of the updated reservation.
     */
    @Operation(summary = "Update the status of a reservation", description = "Allows a guest or host to update the status of a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"guestId\": 1, \"accommodationId\": 1, \"checkInDate\": \"2025-10-01T14:00:00\", \"checkOutDate\": \"2025-10-03T12:00:00\", \"numberOfGuests\": 2, \"totalPrice\": 200.00, \"status\": \"CONFIRMED\", \"createdAt\": \"2025-09-10T22:00:00\", \"updatedAt\": \"2025-09-10T22:10:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Reservation not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"The reservation does not exist\", \"code\": 404}"))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"You do not have permission to update this reservation\", \"code\": 403}")))
    })
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable @Parameter(description = "Reservation ID") Long reservationId,
            @Valid @RequestBody @Schema(description = "Details for updating the reservation") ReservationUpdateDTO updateDTO,
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        ReservationResponseDTO response = reservationService.updateReservation(reservationId, updateDTO, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a reservation.
     *
     * @param reservationId  The ID of the reservation to cancel.
     * @param authentication The authenticated user's details.
     * @return A {@link ResponseEntity} with no content, indicating successful cancellation.
     */
    @Operation(summary = "Cancel a reservation", description = "Allows a guest or host to cancel a reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation successfully canceled"),
            @ApiResponse(responseCode = "404", description = "Reservation not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"The reservation does not exist\", \"code\": 404}"))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"You do not have permission to cancel this reservation\", \"code\": 403}")))
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable @Parameter(description = "Reservation ID") Long reservationId,
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        reservationService.cancelReservation(reservationId, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves reservations for a guest, optionally filtered by status.
     *
     * @param guestId        The ID of the guest.
     * @param status         The reservation status to filter by (optional).
     * @param authentication The authenticated user's details.
     * @return A list of reservation details.
     */
    @Operation(summary = "Retrieve reservations by guest", description = "Gets all reservations for a guest, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": 1, \"guestId\": 1, \"accommodationId\": 1, \"checkInDate\": \"2025-10-01T14:00:00\", \"checkOutDate\": \"2025-10-03T12:00:00\", \"numberOfGuests\": 2, \"totalPrice\": 200.00, \"status\": \"PENDING\", \"createdAt\": \"2025-09-10T22:00:00\", \"updatedAt\": \"2025-09-10T22:00:00\"}]"))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"You do not have permission to view this user's reservations\", \"code\": 403}")))
    })
    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByGuest(
            @PathVariable @Parameter(description = "Guest ID") Long guestId,
            @RequestParam(required = false) @Parameter(description = "Filter by reservation status (optional)") String status,
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<ReservationResponseDTO> response = reservationService.getReservationsByGuest(guestId, username, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves reservations for an accommodation, optionally filtered by status.
     *
     * @param accommodationId The ID of the accommodation.
     * @param status          The reservation status to filter by (optional).
     * @param authentication  The authenticated user's details.
     * @return A list of reservation details.
     */
    @Operation(summary = "Retrieve reservations by accommodation", description = "Gets all reservations for an accommodation, optionally filtered by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations successfully retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": 1, \"guestId\": 1, \"accommodationId\": 1, \"checkInDate\": \"2025-10-01T14:00:00\", \"checkOutDate\": \"2025-10-03T12:00:00\", \"numberOfGuests\": 2, \"totalPrice\": 200.00, \"status\": \"PENDING\", \"createdAt\": \"2025-09-10T22:00:00\", \"updatedAt\": \"2025-09-10T22:00:00\"}]"))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"You do not have permission to view these reservations\", \"code\": 403}")))
    })
    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID") Long accommodationId,
            @RequestParam(required = false) @Parameter(description = "Filter by reservation status (optional)") String status,
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<ReservationResponseDTO> response = reservationService.getReservationsByAccommodation(accommodationId, username, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to extract the username from the current {@link Authentication} object.
     *
     * @param authentication The authenticated user's details.
     * @return The username of the authenticated user.
     */
    private String getAuthenticatedUsername(Authentication authentication) {
        return authentication.getName();
    }
}