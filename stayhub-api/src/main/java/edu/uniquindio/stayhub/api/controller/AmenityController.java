package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.accommodation.AmenityDTO;
import edu.uniquindio.stayhub.api.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<AmenityDTO>> getAllActiveAmenities() {
        LOGGER.info("Listing all active amenities");
        List<AmenityDTO> amenities = amenityService.getAllActiveAmenities();

        return amenities.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(amenities, HttpStatus.OK);
    }

    @Operation(summary = "Create a new amenity")
    @PostMapping
    public ResponseEntity<AmenityDTO> createAmenity(
            @RequestBody @Valid AmenityDTO amenityDTO) {
        LOGGER.info("Request to create new amenity: {}", amenityDTO.getName());
        AmenityDTO createdAmenity = amenityService.createAmenity(amenityDTO);
        return new ResponseEntity<>(createdAmenity, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing amenity")
    @PutMapping("/{id}")
    public ResponseEntity<AmenityDTO> updateAmenity(
            @PathVariable @Parameter(description = "Amenity ID", required = true) Long id,
            @RequestBody @Valid AmenityDTO amenityDTO) {
        LOGGER.info("Request to update amenity with ID: {}", id);
        AmenityDTO updatedAmenity = amenityService.updateAmenity(id, amenityDTO);
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
    public ResponseEntity<AmenityDTO> getAmenityById(
            @PathVariable @Parameter(description = "Amenity ID", required = true) Long id) {
        LOGGER.info("Request to get amenity with ID: {}", id);
        AmenityDTO amenity = amenityService.getAmenityById(id);
        return new ResponseEntity<>(amenity, HttpStatus.OK);
    }
}