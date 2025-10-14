package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.amenity.AmenityRequestDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityResponseDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AmenityNotFoundException;
import edu.uniquindio.stayhub.api.exception.InactiveAmenityException;
import edu.uniquindio.stayhub.api.mapper.AmenityMapper;
import edu.uniquindio.stayhub.api.model.Amenity;
import edu.uniquindio.stayhub.api.repository.AmenityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private AmenityMapper amenityMapper;

    @InjectMocks
    private AmenityService amenityService;

    private Long amenityId = 1L;
    private Amenity amenity;
    private AmenityRequestDTO amenityRequestDTO;
    private AmenityResponseDTO amenityResponseDTO;

    @BeforeEach
    void setup() {
        amenityId = 1L;

        amenity = new Amenity();
        amenity.setId(amenityId);
        amenity.setName("Piscina");
        amenity.setDescription("Piscina grande pa nadar");
        amenity.setActive(true);

        amenityRequestDTO = new AmenityRequestDTO("Piscina", "Piscina grande pa nadar");
        amenityResponseDTO = new AmenityResponseDTO(amenityId, "Piscina", "Piscina grande pa nadar", true);
    }

    @Test
    @DisplayName("Should return a list of active amenities")
    void getAllActiveAmenities_ShouldReturnList() {
        // Arrange
        List<Amenity> activeAmenities = List.of(amenity);
        when(amenityRepository.findByActiveTrueOrderByName()).thenReturn(activeAmenities);
        when(amenityMapper.toResponseDTO(amenity)).thenReturn(amenityResponseDTO);

        // Act
        List<AmenityResponseDTO> result = amenityService.getAllActiveAmenities();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Piscina");
        verify(amenityRepository, times(1)).findByActiveTrueOrderByName();
        verify(amenityMapper, times(1)).toResponseDTO(amenity);
    }

    @Test
    @DisplayName("Should return an empty list when no active amenities are found")
    void getAllActiveAmenities_ShouldReturnEmptyList() {
        // Arrange
        when(amenityRepository.findByActiveTrueOrderByName()).thenReturn(Collections.emptyList());

        // Act
        List<AmenityResponseDTO> result = amenityService.getAllActiveAmenities();

        // Assert
        assertThat(result).isEmpty();
        verify(amenityRepository, times(1)).findByActiveTrueOrderByName();
        verify(amenityMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Should create and return a new amenity successfully")
    void createAmenity_Success() {
        // Arrange
        AmenityRequestDTO newAmenityRequestDTO = new AmenityRequestDTO("Jacuzzi", "Jacuzzi grande");
        Amenity newAmenity = new Amenity();
        newAmenity.setName("Jacuzzi");
        newAmenity.setDescription("Jacuzzi grande");

        Amenity savedAmenity = new Amenity();
        savedAmenity.setId(2L);
        savedAmenity.setName("Jacuzzi");
        savedAmenity.setDescription("Jacuzzi grande");
        savedAmenity.setActive(true);

        AmenityResponseDTO savedAmenityResponseDTO = new AmenityResponseDTO(2L, "Jacuzzi", "Jacuzzi grande", true);

        when(amenityRepository.existsByName(newAmenityRequestDTO.getName())).thenReturn(false);
        when(amenityMapper.requestToEntity(newAmenityRequestDTO)).thenReturn(newAmenity);
        when(amenityRepository.save(newAmenity)).thenReturn(savedAmenity);
        when(amenityMapper.toResponseDTO(savedAmenity)).thenReturn(savedAmenityResponseDTO);

        // Act
        AmenityResponseDTO result = amenityService.createAmenity(newAmenityRequestDTO);

        // Assert
        assertThat(result.getName()).isEqualTo("Jacuzzi");
        assertThat(result.getActive()).isTrue();
        verify(amenityRepository, times(1)).save(newAmenity);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amenity name already exists")
    void createAmenity_NameConflict_ShouldThrowException() {
        // Arrange
        when(amenityRepository.existsByName(amenityRequestDTO.getName())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> amenityService.createAmenity(amenityRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ya existe un servicio con ese nombre");
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update amenity name and description successfully")
    void updateAmenity_Success() {
        // Arrange
        AmenityUpdateDTO updateDTO = new AmenityUpdateDTO("Piscina Nueva", "Piscina grande renovada", true);
        Amenity updatedEntity = new Amenity();
        updatedEntity.setId(amenityId);
        updatedEntity.setName("Piscina Nueva");
        updatedEntity.setDescription("Piscina grande renovada");
        updatedEntity.setActive(true);

        AmenityResponseDTO updatedResponseDTO = new AmenityResponseDTO(amenityId, "Piscina Nueva", "Piscina grande renovada", true);

        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(amenity));
        when(amenityRepository.existsByName("Piscina Nueva")).thenReturn(false);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(updatedEntity);
        when(amenityMapper.toResponseDTO(updatedEntity)).thenReturn(updatedResponseDTO);

        // Act
        AmenityResponseDTO result = amenityService.updateAmenity(amenityId, updateDTO);

        // Assert
        assertThat(result.getName()).isEqualTo("Piscina Nueva");
        assertThat(result.getDescription()).isEqualTo("Piscina grande renovada");
        verify(amenityRepository, times(1)).save(amenity);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when updating non-existing amenity")
    void updateAmenity_NotFound_ShouldThrowException() {
        // Arrange
        AmenityUpdateDTO updateDTO = new AmenityUpdateDTO("Piscina Nueva", "Descripción", true);
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> amenityService.updateAmenity(amenityId, updateDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("El servicio no existe");
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating name to an existing one")
    void updateAmenity_NameConflict_ShouldThrowException() {
        // Arrange
        AmenityUpdateDTO conflictDTO = new AmenityUpdateDTO("Gimnasio", "Gimnasio grande", true);

        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(amenity)); // Original name: Piscina
        when(amenityRepository.existsByName("Gimnasio")).thenReturn(true); // Conflict found

        // Act & Assert
        assertThatThrownBy(() -> amenityService.updateAmenity(amenityId, conflictDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ya existe un servicio con ese nombre");
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set amenity to inactive successfully")
    void deactivateAmenity_Success() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(amenity));
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        // Act
        amenityService.deactivateAmenity(amenityId);

        // Assert
        verify(amenityRepository, times(1)).findById(amenityId);
        verify(amenityRepository, times(1)).save(amenity);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when deactivating non-existing amenity")
    void deactivateAmenity_NotFound_ShouldThrowException() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> amenityService.deactivateAmenity(amenityId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("El servicio no existe");
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return AmenityResponseDTO when fetching existing active amenity")
    void getAmenityById_Active_ShouldReturnDTO() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(amenity));
        when(amenityMapper.toResponseDTO(amenity)).thenReturn(amenityResponseDTO);

        // Act
        AmenityResponseDTO result = amenityService.getAmenityById(amenityId);

        // Assert
        assertThat(result.getName()).isEqualTo("Piscina");
        verify(amenityRepository, times(1)).findById(amenityId);
        verify(amenityMapper, times(1)).toResponseDTO(amenity);
    }

    @Test
    @DisplayName("Should throw AmenityNotFoundException when fetching non-existing amenity")
    void getAmenityById_NotFound_ShouldThrowException() {
        // Arrange
        when(amenityRepository.findById(amenityId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> amenityService.getAmenityById(amenityId))
                .isInstanceOf(AmenityNotFoundException.class)
                .hasMessage("Amenity not found with ID: " + amenityId);
    }

    @Test
    @DisplayName("Should throw InactiveAmenityException when fetching existing but inactive amenity")
    void getAmenityById_Inactive_ShouldThrowException() {
        // Arrange
        Amenity inactiveAmenity = new Amenity();
        inactiveAmenity.setId(amenityId);
        inactiveAmenity.setName("Piscina Inactiva");
        inactiveAmenity.setDescription("Piscina inactiva");
        inactiveAmenity.setActive(false);

        when(amenityRepository.findById(amenityId)).thenReturn(Optional.of(inactiveAmenity));

        // Act & Assert
        assertThatThrownBy(() -> amenityService.getAmenityById(amenityId))
                .isInstanceOf(InactiveAmenityException.class)
                .hasMessage("Amenity with ID " + amenityId + " is inactive");
        verify(amenityMapper, never()).toResponseDTO(any());
    }
}