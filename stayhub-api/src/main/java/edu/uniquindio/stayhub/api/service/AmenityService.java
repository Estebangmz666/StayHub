package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.amenity.AmenityRequestDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityResponseDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AmenityNotFoundException;
import edu.uniquindio.stayhub.api.exception.InactiveAmenityException;
import edu.uniquindio.stayhub.api.mapper.AmenityMapper;
import edu.uniquindio.stayhub.api.model.Amenity;
import edu.uniquindio.stayhub.api.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service class for managing amenities.
 * Provides methods for creating, retrieving, updating, and deactivating amenities.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AmenityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmenityService.class);
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    /**
     * Retrieves all active amenities.
     * @return List of AmenityResponseDTO representing active amenities.
     */
    @Transactional(readOnly = true)
    public List<AmenityResponseDTO> getAllActiveAmenities() {
        LOGGER.info("Fetching all active amenities");
        return amenityRepository.findByActiveTrueOrderByName()
                .stream()
                .map(amenityMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new amenity.
     * @param amenityRequestDTO Data of the amenity to create.
     * @return AmenityResponseDTO of the created amenity.
     */
    public AmenityResponseDTO createAmenity(AmenityRequestDTO amenityRequestDTO) {
        LOGGER.info("Creating amenity with name: {}", amenityRequestDTO.getName());

        if (amenityRepository.existsByName(amenityRequestDTO.getName())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }

        Amenity amenity = amenityMapper.requestToEntity(amenityRequestDTO);
        amenity.setActive(true);

        Amenity savedAmenity = amenityRepository.save(amenity);
        LOGGER.debug("Amenity created successfully with ID: {}", savedAmenity.getId());

        return amenityMapper.toResponseDTO(savedAmenity);
    }

    /**
     * Updates an existing amenity.
     * @param id The ID of the amenity to update.
     * @param amenityUpdateDTO The updated data.
     * @return Updated AmenityResponseDTO.
     */
    public AmenityResponseDTO updateAmenity(Long id, AmenityUpdateDTO amenityUpdateDTO) {
        LOGGER.info("Updating amenity with ID: {}", id);

        Amenity existing = amenityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El servicio no existe"));

        if (amenityUpdateDTO.getName() != null
                && !existing.getName().equalsIgnoreCase(amenityUpdateDTO.getName())
                && amenityRepository.existsByName(amenityUpdateDTO.getName())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }

        if (amenityUpdateDTO.getName() != null) {
            existing.setName(amenityUpdateDTO.getName());
        }

        if (amenityUpdateDTO.getDescription() != null) {
            existing.setDescription(amenityUpdateDTO.getDescription());
        }

        if (amenityUpdateDTO.getActive() != null) {
            existing.setActive(amenityUpdateDTO.getActive());
        }

        Amenity updated = amenityRepository.save(existing);
        LOGGER.debug("Amenity updated successfully: {}", updated.getName());

        return amenityMapper.toResponseDTO(updated);
    }

    /**
     * Deactivates (soft delete) an amenity by ID.
     * @param id The ID of the amenity.
     */
    public void deactivateAmenity(Long id) {
        LOGGER.info("Deactivating amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El servicio no existe"));

        amenity.setActive(false);
        amenityRepository.save(amenity);
        LOGGER.debug("Amenity deactivated successfully with ID: {}", id);
    }

    /**
     * Retrieves an active amenity by ID.
     * @param id The ID of the amenity.
     * @return AmenityResponseDTO of the amenity.
     * @throws AmenityNotFoundException if amenity doesn't exist.
     * @throws InactiveAmenityException if amenity is inactive.
     */
    @Transactional(readOnly = true)
    public AmenityResponseDTO getAmenityById(Long id) {
        LOGGER.info("Fetching amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException("Amenity not found with ID: " + id));

        if (!amenity.isActive()) {
            throw new InactiveAmenityException("Amenity with ID " + id + " is inactive");
        }

        return amenityMapper.toResponseDTO(amenity);
    }
}