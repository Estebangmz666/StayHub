package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.dto.auth.Error;
import edu.uniquindio.stayhub.api.service.AccommodationService;
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

@Tag(name = "Accommodation Management", description = "Endpoints for creating, updating, and deleting accommodations")
@RestController
@RequestMapping("/api/v1/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @Operation(summary = "Create a new accommodation", description = "Allows a host to register a new accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accommodation created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Casa en El Poblado\", \"pricePerNight\": 150.000}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El título es obligatorio\", \"code\": 400}")))
    })
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(
            @RequestBody @Schema(description = "Accommodation creation details") AccommodationRequestDTO requestDTO,
            @Parameter(description = "Host ID", required = true) @RequestHeader("X-User-Id") Long hostId) {
        AccommodationResponseDTO response = accommodationService.createAccommodation(requestDTO, hostId);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update an accommodation", description = "Allows a host to update an existing accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccommodationResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Casa en El Poblado Actualizada\"}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}")))
    })
    @PutMapping("/{accommodationId}")
    public ResponseEntity<AccommodationResponseDTO> updateAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID") Long accommodationId,
            @RequestBody @Schema(description = "Accommodation update details") AccommodationUpdateDTO updateDTO) {
        AccommodationResponseDTO response = accommodationService.updateAccommodation(accommodationId, updateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete an accommodation", description = "Allows a host to soft-delete an accommodation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accommodation deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Alojamiento eliminado exitosamente\", \"code\": 200}"))),
            @ApiResponse(responseCode = "404", description = "Accommodation not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El alojamiento no existe\", \"code\": 404}")))
    })
    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<Error> deleteAccommodation(
            @PathVariable @Parameter(description = "Accommodation ID") Long accommodationId) {
        accommodationService.deleteAccommodation(accommodationId);
      return ResponseEntity.ok(new Error("Alojamiento eliminado exitosamente", 200));  
    }
}