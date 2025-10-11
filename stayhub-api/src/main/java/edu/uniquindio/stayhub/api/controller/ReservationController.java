package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.dto.responses.SearchResponseDTO;
import edu.uniquindio.stayhub.api.dto.responses.SuccessResponseDTO;
import edu.uniquindio.stayhub.api.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reservation Management", description = "Endpoints for managing reservations in StayHub")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    @Operation(summary = "Create a new reservation", description = "Allows a guest to create a new reservation for an accommodation")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User is not a guest"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody @Parameter(description = "Reservation creation details") ReservationRequestDTO requestDTO,
            @RequestHeader("X-User-Email") @Parameter(description = "User email", required = true) String username) throws MessagingException {
        LOGGER.info("Processing reservation creation for user: {}", username);
        ReservationResponseDTO response = reservationService.createReservation(requestDTO, username);
        LOGGER.debug("Reservation created successfully with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a reservation by ID", description = "Retrieves the details of a specific reservation by its ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized to view this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservation(
            @PathVariable @Parameter(description = "Reservation ID", required = true) Long id,
            @RequestHeader("X-User-Email") @Parameter(description = "User email", required = true) String username) {
        LOGGER.info("Retrieving reservation with ID: {} for user: {}", id, username);
        ReservationResponseDTO response = reservationService.getReservationById(id, username);
        LOGGER.debug("Reservation retrieved successfully with ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update a reservation", description = "Allows updating the status of an existing reservation")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "User not authorized to update this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable @Parameter(description = "Reservation ID", required = true) Long id,
            @Valid @RequestBody @Parameter(description = "Reservation update details") ReservationUpdateDTO updateDTO,
            @RequestHeader("X-User-Email") @Parameter(description = "User email", required = true) String username) throws MessagingException {
        LOGGER.info("Processing reservation update for ID: {}, user: {}", id, username);
        ReservationResponseDTO response = reservationService.updateReservation(id, updateDTO, username);
        LOGGER.debug("Reservation updated successfully for ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cancel a reservation", description = "Soft deletes a reservation by setting isDeleted to true and status to CANCELLED")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "User not authorized to cancel this reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO> cancelReservation(
            @PathVariable @Parameter(description = "Reservation ID", required = true) Long id,
            @RequestHeader("X-User-Email") @Parameter(description = "User email", required = true) String username) throws MessagingException {
        LOGGER.info("Processing reservation cancellation for ID: {}, user: {}", id, username);
        reservationService.cancelReservation(id, username);
        LOGGER.debug("Reservation cancelled successfully for ID: {}", id);
        return new ResponseEntity<>(new SuccessResponseDTO("Reservation cancelled successfully"), HttpStatus.OK);
    }

    @Operation(summary = "List user's reservations", description = "Retrieves a paginated list of reservations for the authenticated guest, optionally filtered by status")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No reservations found"),
            @ApiResponse(responseCode = "400", description = "Invalid reservation status")
    })
    @GetMapping
    public ResponseEntity<SearchResponseDTO<ReservationResponseDTO>> listMyReservations(
            @RequestHeader("X-User-Email") @Parameter(description = "User email", required = true) String username,
            @RequestParam(required = false) @Parameter(description = "Reservation status filter (e.g., PENDING, CONFIRMED, CANCELLED)", example = "PENDING") String status,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of items per page", example = "10") @Positive int size) {
        LOGGER.info("Retrieving reservations for user: {}, status: {}", username, status);

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationResponseDTO> result = reservationService.getReservationsByGuest(username, status, pageable);

        SearchResponseDTO<ReservationResponseDTO> response = new SearchResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
        LOGGER.debug("Retrieved {} reservations for user: {}", result.getTotalElements(), username);

        return result.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "List accommodation reservations", description = "Retrieves a paginated list of reservations for a specific accommodation (only for the host). Optionally filtered by status")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No reservations found"),
            @ApiResponse(responseCode = "400", description = "Invalid reservation status"),
            @ApiResponse(responseCode = "403", description = "User not authorized to view these reservations"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/accommodations/{accommodationId}")
    public ResponseEntity<SearchResponseDTO<ReservationResponseDTO>> listAccommodationReservations(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long accommodationId,
            @RequestHeader("X-User-Email") @Parameter(description = "User email (host)", required = true) String username,
            @RequestParam(required = false) @Parameter(description = "Reservation status filter (e.g., PENDING, CONFIRMED, CANCELLED)", example = "PENDING") String status,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of items per page", example = "10") @Positive int size) {
        LOGGER.info("Retrieving reservations for accommodation ID: {}, requested by host: {}, status: {}", accommodationId, username, status);

        Pageable pageable = PageRequest.of(page, size);
        Page<ReservationResponseDTO> result = reservationService.getReservationsByAccommodation(accommodationId, username, status, pageable);

        SearchResponseDTO<ReservationResponseDTO> response = new SearchResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
        LOGGER.debug("Retrieved {} reservations for accommodation ID: {}", result.getTotalElements(), accommodationId);

        return result.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(response, HttpStatus.OK);
    }
}