package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub.api.mapper.AccommodationMapper;
import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {

    @Mock private AccommodationRepository accommodationRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccommodationMapper accommodationMapper;
    @Mock private NotificationService notificationService;

    @InjectMocks private AccommodationService accommodationService;

    private User hostUser;
    private User guestUser;
    private Accommodation accommodation;
    private AccommodationRequestDTO requestDTO;
    private AccommodationUpdateDTO updateDTO;
    private AccommodationResponseDTO responseDTO;
    private String hostEmail;
    private String guestEmail;
    private Long accommodationId;

    @BeforeEach
    public void setup() {
        hostEmail = "host@example.com";
        guestEmail = "guest@example.com";
        accommodationId = 1L;

        hostUser = new User();
        hostUser.setId(1L);
        hostUser.setEmail(hostEmail);
        hostUser.setRole(Role.HOST);

        guestUser = new User();
        guestUser.setId(2L);
        guestUser.setEmail(guestEmail);
        guestUser.setRole(Role.GUEST);

        requestDTO = new AccommodationRequestDTO("Casa en el Poblado", "Casa con 3 habitaciones", 4, 12.0, 12.0, "Cll 4a#123-34", "Medellin", BigDecimal.valueOf(120000), "ValidUrlImage" ,List.of("img1", "img2"));
        updateDTO = new AccommodationUpdateDTO("Casa Actualizada", "Casa renovada", 5, 13.0, 13.0, "Cll 5a#123-34", "Medellin", BigDecimal.valueOf(150000), "newUrlImage", List.of("img3", "img4"));
        accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setTitle("Casa en el Poblado");
        accommodation.setHost(hostUser);
        accommodation.setDeleted(false);
        responseDTO = new AccommodationResponseDTO(accommodationId, "Casa en el Poblado", "Casa con 3 habitaciones", 4, "validUrlImage", 12.0, 12.0, "Cll 4a#123-34", "Medellín", BigDecimal.valueOf(120000), List.of("img1", "img2"));
    }

    @Test
    @DisplayName("Should create accommodation and return DTO when user is host")
    public void createAccommodation_ValidHost_ShouldReturnCreatedAccommodation() throws MessagingException {
        // Arrange
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationMapper.toEntity(requestDTO)).thenReturn(accommodation);
        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(accommodation);
        when(accommodationMapper.toResponseDTO(accommodation)).thenReturn(responseDTO);

        // Act
        AccommodationResponseDTO result = accommodationService.createAccommodation(requestDTO, hostEmail);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(accommodationId);
        assertThat(result.getTitle()).isEqualTo("Casa en el Poblado");
        assertThat(result.getPricePerNight()).isEqualTo(BigDecimal.valueOf(120000));
        verify(userRepository, times(1)).findByEmail(hostEmail);
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when a non-host user tries to create an accommodation")
    public void createAccommodation_NonHostUser_ShouldThrowAccessDenied() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.createAccommodation(requestDTO, guestEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Solo los anfitriones pueden crear alojamientos");
        verify(userRepository, times(1)).findByEmail(guestEmail);
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is not found")
    public void createAccommodation_UserNotFound_ShouldThrowAccessDenied() {
        // Arrange
        String unknownEmail = "unknown@example.com";
        when(userRepository.findByEmail(unknownEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.createAccommodation(requestDTO, unknownEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Usuario no encontrado");
        verify(userRepository, times(1)).findByEmail(unknownEmail);
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should update accommodation and return updated DTO when user is the host owner")
    public void updateAccommodation_ValidHostOwner_ShouldReturnUpdatedAccommodation() throws MessagingException {
        // Arrange
        Accommodation updatedAccommodation = new Accommodation();
        updatedAccommodation.setId(accommodationId);
        updatedAccommodation.setTitle("Casa Actualizada");
        updatedAccommodation.setHost(hostUser);
        AccommodationResponseDTO updatedResponseDTO = new AccommodationResponseDTO(accommodationId, "Casa Actualizada", "Casa renovada", 5, "newUrlImage", 13.0, 13.0, "Cll 5a#123-34", "Medellín", BigDecimal.valueOf(150000), List.of("img3", "img4"));

        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        doNothing().when(accommodationMapper).updateEntity(updateDTO, accommodation);
        when(accommodationRepository.save(updatedAccommodation)).thenReturn(updatedAccommodation);
        when(accommodationMapper.toResponseDTO(updatedAccommodation)).thenReturn(updatedResponseDTO);

        // Act
        AccommodationResponseDTO result = accommodationService.updateAccommodation(accommodationId, updateDTO, hostEmail);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(accommodationId);
        assertThat(result.getTitle()).isEqualTo("Casa Actualizada");
        assertThat(result.getPricePerNight()).isEqualTo(BigDecimal.valueOf(150000));
        verify(userRepository, times(1)).findByEmail(hostEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationMapper, times(1)).updateEntity(updateDTO, accommodation);
        verify(accommodationRepository, times(1)).save(updatedAccommodation);
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when updating with non-host user")
    public void updateAccommodation_NonHostUser_ShouldThrowAccessDenied() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.updateAccommodation(accommodationId, updateDTO, guestEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para update este alojamiento");
        verify(userRepository, times(1)).findByEmail(guestEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationMapper, never()).updateEntity(any(), any());
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when updating non-existing accommodation")
    public void updateAccommodation_NotFound_ShouldThrowNotFound() {
        // Arrange
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.updateAccommodation(accommodationId, updateDTO, hostEmail))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("El alojamiento no existe");
        verify(userRepository, times(1)).findByEmail(hostEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationMapper, never()).updateEntity(any(), any());
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should delete accommodation when user is the host owner")
    public void deleteAccommodation_ValidHostOwner_ShouldDeleteSuccessfully() throws MessagingException {
        // Arrange
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);

        // Act
        accommodationService.deleteAccommodation(accommodationId, hostEmail);

        // Assert
        assertThat(accommodation.isDeleted()).isTrue();
        verify(userRepository, times(1)).findByEmail(hostEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationRepository, times(1)).save(accommodation);
        verify(notificationService, times(1)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when deleting with non-host user")
    public void deleteAccommodation_NonHostUser_ShouldThrowAccessDenied() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.deleteAccommodation(accommodationId, guestEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para delete este alojamiento");
        verify(userRepository, times(1)).findByEmail(guestEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when deleting non-existing accommodation")
    public void deleteAccommodation_NotFound_ShouldThrowNotFound() {
        // Arrange
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.deleteAccommodation(accommodationId, hostEmail))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("El alojamiento no existe");
        verify(userRepository, times(1)).findByEmail(hostEmail);
        verify(accommodationRepository, times(1)).findById(accommodationId);
        verify(accommodationRepository, never()).save(any(Accommodation.class));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when updating deleted accommodation")
    public void updateAccommodation_DeletedAccommodation_ShouldThrowNotFound() {
        // Arrange
        accommodation.setDeleted(true);
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.updateAccommodation(accommodationId, updateDTO, hostEmail))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("El alojamiento no existe");
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when deleting already deleted accommodation")
    public void deleteAccommodation_AlreadyDeleted_ShouldThrowNotFound() {
        // Arrange
        accommodation.setDeleted(true);
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.deleteAccommodation(accommodationId, hostEmail))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("El alojamiento no existe");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when host tries to modify another host's accommodation")
    public void updateAccommodation_DifferentHostOwner_ShouldThrowAccessDenied() {
        // Arrange
        User anotherHost = new User();
        anotherHost.setId(3L);
        anotherHost.setEmail("anotherhost@example.com");
        anotherHost.setRole(Role.HOST);

        when(userRepository.findByEmail("anotherhost@example.com")).thenReturn(Optional.of(anotherHost));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.updateAccommodation(accommodationId, updateDTO, "anotherhost@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para update este alojamiento");
    }

    @Test
    @DisplayName("Should return AccommodationResponseDTO when fetching existing, non-deleted accommodation")
    public void getAccommodation_ExistingNonDeleted_ShouldReturnDTO() {
        // Arrange
        when(accommodationRepository.findByIdAndDeletedFalse(accommodationId)).thenReturn(Optional.of(accommodation));
        when(accommodationMapper.toResponseDTO(accommodation)).thenReturn(responseDTO);

        // Act
        AccommodationResponseDTO result = accommodationService.getAccommodation(accommodationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(accommodationId);
        verify(accommodationRepository, times(1)).findByIdAndDeletedFalse(accommodationId);
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException when fetching non-existing or deleted accommodation")
    public void getAccommodation_NotFoundOrDeleted_ShouldThrowNotFound() {
        // Arrange
        when(accommodationRepository.findByIdAndDeletedFalse(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.getAccommodation(accommodationId))
                .isInstanceOf(AccommodationNotFoundException.class)
                .hasMessage("El alojamiento no existe");
        verify(accommodationRepository, times(1)).findByIdAndDeletedFalse(accommodationId);
    }

    @Test
    @DisplayName("Should return paginated list of active accommodations")
    public void listAccommodations_ShouldReturnPageOfDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodationList, pageable, 1);

        when(accommodationRepository.findAllByDeletedFalse(pageable)).thenReturn(accommodationPage);
        when(accommodationMapper.toResponseDTO(any(Accommodation.class))).thenReturn(responseDTO);

        // Act
        Page<AccommodationResponseDTO> result = accommodationService.listAccommodations(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(accommodationId);
        verify(accommodationRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no active accommodations are found")
    public void listAccommodations_NoResults_ShouldReturnEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Accommodation> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(accommodationRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);

        // Act
        Page<AccommodationResponseDTO> result = accommodationService.listAccommodations(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(accommodationRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(accommodationMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Should return paginated list of accommodations matching search criteria")
    public void searchAccommodations_ValidFilters_ShouldReturnPageOfDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String city = "Medellin";
        Integer minCapacity = 4;
        BigDecimal maxPrice = BigDecimal.valueOf(200000);
        List<Long> amenityIds = List.of(1L, 2L);

        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> searchPage = new PageImpl<>(accommodationList, pageable, 1);

        when(accommodationRepository.findByFilters(city, minCapacity, maxPrice, amenityIds, pageable))
                .thenReturn(searchPage);
        when(accommodationMapper.toResponseDTO(any(Accommodation.class))).thenReturn(responseDTO);

        // Act
        Page<AccommodationResponseDTO> result = accommodationService.searchAccommodations(city, minCapacity, maxPrice, amenityIds, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(accommodationRepository, times(1)).findByFilters(city, minCapacity, maxPrice, amenityIds, pageable);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when amenityIds contains null values")
    public void searchAccommodations_NullAmenityId_ShouldThrowException() {
        // Arrange
        List<Long> amenityIdsWithNull = List.of(1L, null, 2L);
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThatThrownBy(() -> accommodationService.searchAccommodations("City", 2, BigDecimal.TEN, amenityIdsWithNull, pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Las listas de IDs de amenidades no pueden contener valores nulos");
        verify(accommodationRepository, never()).findByFilters(any(), any(), any(), any(), any());
    }
}