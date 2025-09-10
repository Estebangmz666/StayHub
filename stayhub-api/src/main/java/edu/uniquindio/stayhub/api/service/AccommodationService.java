package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing accommodation-related operations.
 */
@Service
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    @Autowired
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
        accommodation.setHost(host);
        accommodation.setDeleted(false);

        // Save to repository
        accommodation = accommodationRepository.save(accommodation);

        // Map to response DTO
        return mapToResponseDTO(accommodation);
    }

    /**
     * Updates existing accommodation based on the provided ID and update DTO.
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
        accommodation.setTitle(updateDTO.getTitle());
        accommodation.setDescription(updateDTO.getDescription());
        accommodation.setCity(updateDTO.getCity());
        accommodation.setPricePerNight(updateDTO.getPricePerNight());
        accommodation.setCapacity(updateDTO.getCapacity());
        accommodation.setLongitude(updateDTO.getLongitude());
        accommodation.setLatitude(updateDTO.getLatitude());
        accommodation.setLocationDescription(updateDTO.getLocationDescription());
        accommodation.setMainImage(updateDTO.getMainImage());
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
        accommodation.setDeleted(true);
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
        responseDTO.setCapacity(accommodation.getCapacity());
        responseDTO.setMainImage(accommodation.getMainImage());
        responseDTO.setLongitude(accommodation.getLongitude());
        responseDTO.setLatitude(accommodation.getLatitude());
        responseDTO.setLocationDescription(accommodation.getLocationDescription());
        responseDTO.setCity(accommodation.getCity());
        responseDTO.setPricePerNight(accommodation.getPricePerNight());
        responseDTO.setImages(accommodation.getImages());
        return responseDTO;
    }
}