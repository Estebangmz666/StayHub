package edu.uniquindio.stayhub.api.service;

import org.springframework.stereotype.Service;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;

/**
 * Service layer for managing accommodation-related operations.
 */
@Service
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    public AccommodationService(AccommodationRepository accommodationRepository, UserRepository userRepository) {
        this.accommodationRepository = accommodationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates new accommodation based on the provided request DTO.
     *
     * @param requestDTO The accommodation creation details
     * @param hostId The ID of the host creating the accommodation (from authentication context)
     * @return AccommodationResponseDTO with the created accommodation details
     * @throws UserNotFoundException if the host is not found
     */
    public AccommodationResponseDTO createAccommodation(AccommodationRequestDTO requestDTO, Long hostId) {
        // Find the host
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new UserNotFoundException("El host no existe"));

        // Map requestDTO to Accommodation entity
        Accommodation accommodation = new Accommodation();
        accommodation.setTitle(requestDTO.getTitle());
        accommodation.setDescription(requestDTO.getDescription());
        accommodation.setCity(requestDTO.getCity());
        accommodation.setPricePerNight(requestDTO.getPricePerNight());
        accommodation.setCapacity(requestDTO.getCapacity());
        accommodation.setLongitude(requestDTO.getLongitude());
        accommodation.setLatitude(requestDTO.getLatitude());
        accommodation.setLocationDescription(requestDTO.getLocationDescription());
        accommodation.setMainImage(requestDTO.getMainImage());
        accommodation.setImages(requestDTO.getImages());
        accommodation.setHost(host); // Set the host relationship
        accommodation.setDeleted(false); // Explicitly set to false (default is handled by DB)

        // Save to repository
        accommodation = accommodationRepository.save(accommodation);

        // Map to response DTO
        return mapToResponseDTO(accommodation);
    }

    /**
     * Updates an existing accommodation based on the provided ID and update DTO.
     *
     * @param accommodationId The ID of the accommodation to update
     * @param updateDTO The accommodation update details
     * @return AccommodationResponseDTO with the updated accommodation details
     * @throws UserNotFoundException if the accommodation is not found
     */
    public AccommodationResponseDTO updateAccommodation(Long accommodationId, AccommodationUpdateDTO updateDTO) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new UserNotFoundException("El alojamiento no existe"));

        // Update fields from updateDTO (only non-null values)
        accommodation.setTitle(updateDTO.getTitle() != null ? updateDTO.getTitle() : accommodation.getTitle());
        accommodation.setDescription(updateDTO.getDescription() != null ? updateDTO.getDescription() : accommodation.getDescription());
        accommodation.setCity(updateDTO.getCity() != null ? updateDTO.getCity() : accommodation.getCity());
        accommodation.setPricePerNight(updateDTO.getPricePerNight() != null ? updateDTO.getPricePerNight() : accommodation.getPricePerNight());
        accommodation.setCapacity(updateDTO.getCapacity() != null ? updateDTO.getCapacity() : accommodation.getCapacity());
        accommodation.setLongitude(updateDTO.getLongitude() != null ? updateDTO.getLongitude() : accommodation.getLongitude());
        accommodation.setLatitude(updateDTO.getLatitude() != null ? updateDTO.getLatitude() : accommodation.getLatitude());
        accommodation.setLocationDescription(updateDTO.getLocationDescription() != null ? updateDTO.getLocationDescription() : accommodation.getLocationDescription());
        accommodation.setMainImage(updateDTO.getMainImage() != null ? updateDTO.getMainImage() : accommodation.getMainImage());
        accommodation.setImages(updateDTO.getImages() != null ? updateDTO.getImages() : accommodation.getImages());

        // Save updated accommodation
        accommodation = accommodationRepository.save(accommodation);

        // Map to response DTO
        return mapToResponseDTO(accommodation);
    }

    /**
     * Soft-deletes accommodation by setting isDeleted to true.
     *
     * @param accommodationId The ID of the accommodation to delete
     * @throws UserNotFoundException if the accommodation is not found
     */
    public void deleteAccommodation(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new UserNotFoundException("El alojamiento no existe"));
        accommodation.setDeleted(true); // Uses the existing setter from Lombok
        accommodationRepository.save(accommodation);
    }

    /**
     * Maps an Accommodation entity to an AccommodationResponseDTO.
     *
     * @param accommodation The accommodation entity
     * @return AccommodationResponseDTO with mapped fields
     */
    private AccommodationResponseDTO mapToResponseDTO(Accommodation accommodation) {
        AccommodationResponseDTO responseDTO = new AccommodationResponseDTO();
        responseDTO.setId(accommodation.getId());
        responseDTO.setTitle(accommodation.getTitle());
        responseDTO.setDescription(accommodation.getDescription());
        responseDTO.setCity(accommodation.getCity());
        responseDTO.setPricePerNight(accommodation.getPricePerNight());
        responseDTO.setCapacity(accommodation.getCapacity());
        responseDTO.setLongitude(accommodation.getLongitude());
        responseDTO.setLatitude(accommodation.getLatitude());
        responseDTO.setLocationDescription(accommodation.getLocationDescription());
        responseDTO.setMainImage(accommodation.getMainImage());
        responseDTO.setImages(accommodation.getImages());
        // No incluimos hostId directamente, pero puedes añadirlo si lo necesitas
        return responseDTO;
    }
}