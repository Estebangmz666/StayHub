package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.amenity.AmenityRequestDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityResponseDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityUpdateDTO;
import edu.uniquindio.stayhub.api.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing amenities.
 * Provides endpoints for creating, updating, listing, and deactivating amenities.
 */
@RestController
@RequestMapping("/api/v1/amenities")
@Tag(name = "Amenities", description = "Endpoints for managing accommodation amenities")
@RequiredArgsConstructor
public class AmenityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityController.class);
    private final AmenityService amenityService;

    @Operation(summary = "List all active amenities")
    @GetMapping
    public ResponseEntity<List<AmenityResponseDTO>> getAllActiveAmenities() {
        LOGGER.info("Listing all active amenities");
        List<AmenityResponseDTO> amenities = amenityService.getAllActiveAmenities();

        return amenities.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(amenities, HttpStatus.OK);
    }

    @Operation(summary = "Create a new amenity")
    @PostMapping
    public ResponseEntity<AmenityResponseDTO> createAmenity(
            @Valid @RequestBody AmenityRequestDTO amenityRequestDTO) {
        LOGGER.info("Request to create new amenity: {}", amenityRequestDTO.getName());
        LOGGER.info("Request body: {}", amenityRequestDTO);
        AmenityResponseDTO createdAmenity = amenityService.createAmenity(amenityRequestDTO);
        return new ResponseEntity<>(createdAmenity, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing amenity")
    @PutMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> updateAmenity(
            @PathVariable @Parameter(description = "Amenity ID", required = true) Long id,
            @RequestBody AmenityUpdateDTO amenityUpdateDTO) {
        LOGGER.info("Request to update amenity with ID: {}", id);
        AmenityResponseDTO updatedAmenity = amenityService.updateAmenity(id, amenityUpdateDTO);
        return new ResponseEntity<>(updatedAmenity, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate (soft delete) an amenity")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateAmenity(
            @PathVariable @Parameter(description = "Amenity ID", required = true) Long id) {
        LOGGER.info("Request to deactivate amenity with ID: {}", id);
        amenityService.deactivateAmenity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get amenity details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AmenityResponseDTO> getAmenityById(
            @PathVariable @Parameter(description = "Amenity ID", required = true) Long id) {
        LOGGER.info("Request to get amenity with ID: {}", id);
        AmenityResponseDTO amenity = amenityService.getAmenityById(id);
        return new ResponseEntity<>(amenity, HttpStatus.OK);
    }

    @Operation(summary = "Ping endpoint for health checks", description = "Returns PONG if the controller is alive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controller is alive", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("PONG");
    }
}