package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.service.AccommodationService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing accommodation-related operations.
 * This class provides endpoints for creating, updating, and soft-deleting
 * accommodations by an authenticated host.
 */
@Tag(name = "Accommodation Management", description = "Endpoints for creating, updating, and deleting accommodations")
@RestController
@RequestMapping("/api/v1/accommodations")
@SecurityRequirement(name = "bearerAuth")
public class AccommodationController {

    private final AccommodationService accommodationService;

    /**
     * Constructs an {@code AccommodationController} with the specified service.
     *
     * @param accommodationService The service that handles the business logic for accommodations.
     */
    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    /**
     * Creates new accommodation for the authenticated host.
     *
     * @param requestDTO The details of the accommodation to create.
     * @return A {@link ResponseEntity} containing the details of the newly created accommodation.
     */
    @Operation(summary = "Create a new accommodation", description = "Allows an authenticated host to register a new accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accommodation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Casa en El Poblado\", \"description\": \"Hermosa casa con vista\", \"city\": \"Medellín\", \"pricePerNight\": 150000.00, \"capacity\": 4, \"longitude\": -75.57, \"latitude\": 6.21, \"locationDescription\": \"Cerca al parque\", \"mainImage\": \"https://example.com/image.jpg\", \"images\": [\"https://example.com/image1.jpg\"]}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El título es obligatorio\", \"code\": 400}"))),
            @ApiResponse(responseCode = "403", description = "User is not a host",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Solo los anfitriones pueden crear alojamientos\", \"code\": 403}")))
    })
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(
            @Valid @RequestBody @Schema(description = "Accommodation creation details") AccommodationRequestDTO requestDTO) {
        String username = getAuthenticatedUsername();
        AccommodationResponseDTO response = accommodationService.createAccommodation(requestDTO, username);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Updates an existing accommodation.
     *
     * @param accommodationId The ID of the accommodation to update.
     * @param updateDTO       The updated accommodation details.
     * @return A {@link ResponseEntity} containing the details of the updated accommodation.
     */
    @Operation(summary = "Update an accommodation", description = "Allows an authenticated host to update an existing accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Casa en El Poblado Actualizada\", \"description\": \"Casa renovada\", \"city\": \"Medellín\", \"pricePerNight\": 160000.00, \"capacity\": 4, \"longitude\": -75.57, \"latitude\": 6.21, \"locationDescription\": \"Cerca al parque\", \"mainImage\": \"https://example.com/image.jpg\", \"images\": [\"https://example.com/image1.jpg\"]}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}"))),
            @ApiResponse(responseCode = "403", description = "User is not a host or does not own the accommodation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"No tienes permiso para actualizar este alojamiento\", \"code\": 403}")))
    })
    @PutMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponseDTO> updateAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID") Long accommodationId,
            @Valid @RequestBody @Schema(description = "Accommodation update details") AccommodationUpdateDTO updateDTO) {
        String username = getAuthenticatedUsername();
        AccommodationResponseDTO response = accommodationService.updateAccommodation(accommodationId, updateDTO, username);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft-deletes accommodation.
     *
     * @param accommodationId The ID of the accommodation to delete.
     * @return A {@link ResponseEntity} with no content, indicating successful soft-deletion.
     */
    @Operation(summary = "Delete an accommodation", description = "Allows an authenticated host to soft-delete an accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Accommodation deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}"))),
            @ApiResponse(responseCode = "403", description = "User is not a host or does not own the accommodation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"No tienes permiso para eliminar este alojamiento\", \"code\": 403}")))
    })
    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<Void> deleteAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID") Long accommodationId) {
        String username = getAuthenticatedUsername();
        accommodationService.deleteAccommodation(accommodationId, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper method to extract the username from the current security context.
     * This avoids code duplication across the controller methods.
     *
     * @return The username of the authenticated user.
     */
    private String getAuthenticatedUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}