package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.accommodation.AmenityDTO;
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
    private final AmenityMapper amenityMapper = AmenityMapper.INSTANCE;

    /**
     * Retrieves all active amenities.
     * @return List of AmenityDTO representing active amenities.
     */
    @Transactional(readOnly = true)
    public List<AmenityDTO> getAllActiveAmenities() {
        LOGGER.info("Fetching all active amenities");
        return amenityRepository.findByIsActiveTrueOrderByName()
                .stream()
                .map(amenityMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new amenity.
     * @param amenityDTO Data of the amenity to create.
     * @return AmenityDTO of the created amenity.
     */
    public AmenityDTO createAmenity(AmenityDTO amenityDTO) {
        LOGGER.info("Creating amenity with name: {}", amenityDTO.getName());

        if (amenityRepository.existsByName(amenityDTO.getName())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }

        Amenity amenity = amenityMapper.toEntity(amenityDTO);
        amenity.setActive(true);

        Amenity savedAmenity = amenityRepository.save(amenity);
        LOGGER.debug("Amenity created successfully with ID: {}", savedAmenity.getId());

        return amenityMapper.toDto(savedAmenity);
    }

    /**
     * Updates an existing amenity.
     * @param id The ID of the amenity to update.
     * @param amenityDTO The updated data.
     * @return Updated AmenityDTO.
     */
    public AmenityDTO updateAmenity(Long id, AmenityDTO amenityDTO) {
        LOGGER.info("Updating amenity with ID: {}", id);

        Amenity existing = amenityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El servicio no existe"));

        if (!existing.getName().equalsIgnoreCase(amenityDTO.getName())
                && amenityRepository.existsByName(amenityDTO.getName())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }

        existing.setName(amenityDTO.getName());
        existing.setActive(amenityDTO.isActive());

        Amenity updated = amenityRepository.save(existing);
        LOGGER.debug("Amenity updated successfully: {}", updated.getName());

        return amenityMapper.toDto(updated);
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

    @Transactional(readOnly = true)
    public AmenityDTO getAmenityById(Long id) {
        LOGGER.info("Fetching amenity with ID: {}", id);
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AmenityNotFoundException("Amenity not found with ID: " + id));

        if (!amenity.isActive()) {
            throw new InactiveAmenityException("Amenity with ID " + id + " is inactive");
        }

        return amenityMapper.toDto(amenity);
    }
}