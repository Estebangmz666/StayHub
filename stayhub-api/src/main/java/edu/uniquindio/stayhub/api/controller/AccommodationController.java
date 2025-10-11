package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.responses.SearchResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.dto.responses.SuccessResponseDTO;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Accommodation Management", description = "Endpoints for managing accommodations in StayHub")
@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationController.class);
    private final AccommodationService accommodationService;

    @Operation(summary = "Create a new accommodation", description = "Allows a host to create a new accommodation listing")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accommodation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Modern Apartment\", \"description\": \"Spacious apartment in the city center\", \"capacity\": 4, \"mainImage\": \"https://example.com/images/main.jpg\", \"longitude\": -75.5668, \"latitude\": 6.2442, \"locationDescription\": \"Near El Poblado Park\", \"city\": \"Medellin\", \"pricePerNight\": 150.00, \"images\": [\"https://example.com/images/1.jpg\", \"https://example.com/images/2.jpg\"]}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Price per night must be positive\", \"code\": 400}"))),
            @ApiResponse(responseCode = "403", description = "User is not a host",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Solo los anfitriones pueden crear alojamientos\", \"code\": 403}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Usuario no encontrado\", \"code\": 404}")))
    })
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(
            @RequestHeader("X-Username") @Parameter(description = "User email", required = true) String username,
            @Valid @RequestBody @Parameter(description = "Accommodation creation details") AccommodationRequestDTO requestDTO) throws MessagingException {
        LOGGER.info("Processing accommodation creation for user: {}", username);
        AccommodationResponseDTO response = accommodationService.createAccommodation(requestDTO, username);
        LOGGER.debug("Accommodation created successfully for user: {}", username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get an accommodation by ID", description = "Retrieves the details of a specific accommodation by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Modern Apartment\", \"description\": \"Spacious apartment in the city center\", \"capacity\": 4, \"mainImage\": \"https://example.com/images/main.jpg\", \"longitude\": -75.5668, \"latitude\": 6.2442, \"locationDescription\": \"Near El Poblado Park\", \"city\": \"Medellin\", \"pricePerNight\": 150.00, \"images\": [\"https://example.com/images/1.jpg\", \"https://example.com/images/2.jpg\"]}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationResponseDTO> getAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long id) {
        LOGGER.info("Retrieving accommodation with ID: {}", id);
        AccommodationResponseDTO response = accommodationService.getAccommodation(id);
        LOGGER.debug("Accommodation retrieved successfully with ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update an accommodation", description = "Allows a host to update an existing accommodation")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Updated Apartment\", \"description\": \"Renovated apartment\", \"capacity\": 5, \"mainImage\": \"https://example.com/images/main_v2.jpg\", \"longitude\": -75.5668, \"latitude\": 6.2442, \"locationDescription\": \"Near El Poblado Park\", \"city\": \"Medellin\", \"pricePerNight\": 175.00, \"images\": [\"https://example.com/images/1.jpg\", \"https://example.com/images/2.jpg\"]}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Price per night must be positive\", \"code\": 400}"))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update this accommodation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"No tienes permiso para actualizar este alojamiento\", \"code\": 403}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation or user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}")))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccommodationResponseDTO> updateAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long id,
            @RequestHeader("X-Username") @Parameter(description = "User email", required = true) String username,
            @Valid @RequestBody @Parameter(description = "Accommodation update details") AccommodationUpdateDTO updateDTO) throws MessagingException {
        LOGGER.info("Processing accommodation update for ID: {}, user: {}", id, username);
        AccommodationResponseDTO response = accommodationService.updateAccommodation(id, updateDTO, username);
        LOGGER.debug("Accommodation updated successfully for ID: {}", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Search accommodations with filters", description = "Searches accommodations by city, capacity, price range, and amenities with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodations retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponseDTO.class),
                            examples = @ExampleObject(value = "{\"content\": [{\"id\": 1, \"title\": \"Modern Apartment\", \"description\": \"Spacious apartment\", \"capacity\": 4, \"mainImage\": \"https://example.com/images/main.jpg\", \"longitude\": -75.5668, \"latitude\": 6.2442, \"locationDescription\": \"Near El Poblado Park\", \"city\": \"Medellin\", \"pricePerNight\": 150.00, \"images\": [\"https://example.com/images/1.jpg\"]}], \"page\": 0, \"size\": 10, \"totalElements\": 1}"))),
            @ApiResponse(responseCode = "204", description = "No accommodations found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/search")
    public ResponseEntity<SearchResponseDTO<AccommodationResponseDTO>> searchAccommodations(
            @RequestParam(required = false) @Parameter(description = "City to filter accommodations", example = "Medellin") String city,
            @RequestParam(required = false) @Parameter(description = "Minimum number of guests", example = "2") @Positive Integer minCapacity,
            @RequestParam(required = false) @Parameter(description = "Maximum price per night", example = "200.00") @Positive BigDecimal maxPrice,
            @RequestParam(required = false) @Parameter(description = "List of amenity IDs to filter", example = "[1, 2]") List<Long> amenityIds,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of items per page", example = "10") @Positive int size) {
        LOGGER.info("Searching accommodations with filters: city={}, minCapacity={}, maxPrice={}, amenityIds={}", city, minCapacity, maxPrice, amenityIds);
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationResponseDTO> result = accommodationService.searchAccommodations(city, minCapacity, maxPrice, amenityIds, pageable);
        SearchResponseDTO<AccommodationResponseDTO> response = new SearchResponseDTO<>(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
        LOGGER.debug("Found {} accommodations in search", result.getTotalElements());
        return result.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Delete an accommodation", description = "Soft deletes an accommodation by setting isDeleted to true")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponseDTO.class),
                            examples = @ExampleObject(value = "{\"message\": \"Accommodation deleted successfully\"}"))),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this accommodation",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"No tienes permiso para eliminar este alojamiento\", \"code\": 403}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation or user not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO> deleteAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID", required = true) Long id,
            @RequestHeader("X-Username") @Parameter(description = "User email", required = true) String username) throws MessagingException {
        LOGGER.info("Processing accommodation deletion for ID: {}, user: {}", id, username);
        accommodationService.deleteAccommodation(id, username);
        LOGGER.debug("Accommodation deleted successfully for ID: {}", id);
        return new ResponseEntity<>(new SuccessResponseDTO("Accommodation deleted successfully"), HttpStatus.OK);
    }

    @Operation(summary = "List all accommodations", description = "Retrieves a paginated list of all active accommodations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodations retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchResponseDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": 1, \"title\": \"Modern Apartment\", \"description\": \"Spacious apartment in the city center\", \"capacity\": 4, \"mainImage\": \"https://example.com/images/main.jpg\", \"longitude\": -75.5668, \"latitude\": 6.2442, \"locationDescription\": \"Near El Poblado Park\", \"city\": \"Medellin\", \"pricePerNight\": 150.00, \"images\": [\"https://example.com/images/1.jpg\", \"https://example.com/images/2.jpg\"]}]"))),
            @ApiResponse(responseCode = "204", description = "No accommodations found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<SearchResponseDTO<AccommodationResponseDTO>> listAccommodations(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        LOGGER.info("Retrieving all active accommodations");
        Pageable pageable = PageRequest.of(page, size);
        Page<AccommodationResponseDTO> result = accommodationService.listAccommodations(pageable);
        SearchResponseDTO<AccommodationResponseDTO> response = new SearchResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements()
        );
        LOGGER.debug("Retrieved {} active accommodations", result.getTotalElements());
        return result.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(response, HttpStatus.OK);
    }
}