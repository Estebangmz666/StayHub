package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.AccommodationNotFoundException;
import edu.uniquindio.stayhub.api.exception.InvalidReservationDatesException;
import edu.uniquindio.stayhub.api.exception.ReservationNotFoundException;
import edu.uniquindio.stayhub.api.mapper.ReservationMapper;
import edu.uniquindio.stayhub.api.model.*;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private ReservationMapper reservationMapper;

    @InjectMocks private ReservationService reservationService;

    private User guestUser;
    private User hostUser;
    private User otherUser;
    private Accommodation accommodation;
    private Reservation reservation;
    private ReservationRequestDTO requestDTO;
    private ReservationResponseDTO responseDTO;
    private Pageable pageable;

    private final Long guestId = 1L;
    private final Long hostId = 2L;
    private final Long accommodationId = 10L;
    private final Long reservationId = 100L;
    private final String guestEmail = "guest@test.com";
    private final String hostEmail = "host@test.com";
    private final String otherEmail = "other@test.com";

    @BeforeEach
    void setup() {
        // 1. Usuarios
        guestUser = new User();
        guestUser.setId(guestId);
        guestUser.setEmail(guestEmail);
        guestUser.setRole(Role.GUEST);

        hostUser = new User();
        hostUser.setId(hostId);
        hostUser.setEmail(hostEmail);
        hostUser.setRole(Role.HOST);

        otherUser = new User();
        otherUser.setId(3L);
        otherUser.setEmail(otherEmail);
        otherUser.setRole(Role.GUEST);

        // 2. Alojamiento
        accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setHost(hostUser);
        accommodation.setCapacity(4);
        accommodation.setPricePerNight(BigDecimal.valueOf(100));
        accommodation.setTitle("Cabaña frente al mar");
        accommodation.setDeleted(false);

        // 3. Reserva
        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setGuest(guestUser);
        reservation.setAccommodation(accommodation);
        reservation.setCheckInDate(LocalDateTime.now().plusDays(5));
        reservation.setCheckOutDate(LocalDateTime.now().plusDays(10));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(BigDecimal.valueOf(500));
        reservation.setDeleted(false);

        // 4. DTOs y Pageable
        requestDTO = new ReservationRequestDTO(
                guestId,
                accommodationId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                2
        );
        responseDTO = new ReservationResponseDTO(reservationId, guestId, accommodationId,
                accommodation.getTitle(), ReservationStatus.PENDING, BigDecimal.valueOf(100));

        pageable = PageRequest.of(0, 10);
    }

    // ----------------------------------------------------------------------
    // Tests para createReservation
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should create reservation successfully and send two notifications")
    void createReservation_Success() throws MessagingException {
        // Arrange
        // Mocks comunes
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsOverlappingReservations(anyLong(), any(), any())).thenReturn(false);

        // Mocks para la creación
        when(reservationMapper.toEntity(requestDTO)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(responseDTO);
        doNothing().when(notificationService).createNotification(any(NotificationRequestDTO.class));

        // Act
        ReservationResponseDTO result = reservationService.createReservation(requestDTO, guestEmail);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getAccommodationTitle()).isEqualTo(accommodation.getTitle());
        verify(reservationRepository, times(1)).save(reservation);
        // Debe notificar al huésped y al anfitrión
        verify(notificationService, times(2)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if authenticated user is not a GUEST")
    void createReservation_UserIsNotGuest_ThrowsAccessDenied() {
        // Arrange
        User nonGuestUser = new User();
        nonGuestUser.setId(hostId);
        nonGuestUser.setEmail(hostEmail);
        nonGuestUser.setRole(Role.HOST);

        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(nonGuestUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(requestDTO, hostEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Solo los huéspedes pueden crear reservas");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if guestId in DTO doesn't match authenticated user")
    void createReservation_MismatchedGuestId_ThrowsAccessDenied() {
        // Arrange
        ReservationRequestDTO requestForOther = new ReservationRequestDTO(
                otherUser.getId(), accommodationId, requestDTO.getCheckInDate(), requestDTO.getCheckOutDate(), 2);

        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(requestForOther, guestEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No puedes crear reservas para otro usuario");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccommodationNotFoundException if accommodation does not exist")
    void createReservation_AccommodationNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(requestDTO, guestEmail))
                .isInstanceOf(AccommodationNotFoundException.class);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidReservationDatesException if check-in is after check-out")
    void createReservation_InvalidDates_ThrowsException() {
        // Arrange
        ReservationRequestDTO invalidDatesDTO = new ReservationRequestDTO(
                guestId, accommodationId, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(2), 2);

        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(invalidDatesDTO, guestEmail))
                .isInstanceOf(InvalidReservationDatesException.class)
                .hasMessageContaining("check-in debe ser antes de la fecha de check-out");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidReservationDatesException if accommodation is unavailable")
    void createReservation_Unavailable_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsOverlappingReservations(anyLong(), any(), any())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(requestDTO, guestEmail))
                .isInstanceOf(InvalidReservationDatesException.class)
                .hasMessageContaining("Las fechas seleccionadas no están disponibles");
        verify(reservationRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para updateReservation
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should update reservation status successfully when user is the HOST")
    void updateReservation_Host_Success() throws MessagingException {
        // Arrange
        ReservationUpdateDTO updateDTO = new ReservationUpdateDTO(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(responseDTO);
        doNothing().when(notificationService).createNotification(any(NotificationRequestDTO.class));

        // Act
        ReservationResponseDTO result = reservationService.updateReservation(reservationId, updateDTO, hostEmail);

        // Assert
        assertThat(result).isNotNull();
        verify(reservationMapper, times(1)).updateEntity(updateDTO, reservation);
        verify(reservationRepository, times(1)).save(reservation);
        // Debe notificar al huésped y al anfitrión
        verify(notificationService, times(2)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when updating reservation by unauthorized user")
    void updateReservation_Unauthorized_ThrowsAccessDenied() {
        // Arrange
        ReservationUpdateDTO updateDTO = new ReservationUpdateDTO(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.updateReservation(reservationId, updateDTO, otherEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para actualizar esta reserva");
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ReservationNotFoundException when updating non-existent reservation")
    void updateReservation_NotFound_ThrowsException() {
        // Arrange
        ReservationUpdateDTO updateDTO = new ReservationUpdateDTO(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.updateReservation(reservationId, updateDTO, hostEmail))
                .isInstanceOf(ReservationNotFoundException.class);
        verify(userRepository, never()).findByEmail(anyString());
        verify(reservationRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para cancelReservation
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should cancel reservation successfully (soft delete) when user is the GUEST")
    void cancelReservation_Guest_Success() throws MessagingException {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        doNothing().when(notificationService).createNotification(any(NotificationRequestDTO.class));

        // Act
        reservationService.cancelReservation(reservationId, guestEmail);

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(reservation.isDeleted()).isTrue();
        verify(reservationRepository, times(1)).save(reservation);
        // Debe notificar al huésped y al anfitrión
        verify(notificationService, times(2)).createNotification(any(NotificationRequestDTO.class));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when unauthorized user tries to cancel")
    void cancelReservation_Unauthorized_ThrowsAccessDenied() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.cancelReservation(reservationId, otherEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para cancelar esta reserva");
        verify(reservationRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para getReservationById
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should return reservation when requested by GUEST")
    void getReservationById_Guest_Success() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(responseDTO);

        // Act
        ReservationResponseDTO result = reservationService.getReservationById(reservationId, guestEmail);

        // Assert
        assertThat(result.getId()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("Should return reservation when requested by HOST")
    void getReservationById_Host_Success() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(responseDTO);

        // Act
        ReservationResponseDTO result = reservationService.getReservationById(reservationId, hostEmail);

        // Assert
        assertThat(result.getId()).isEqualTo(reservationId);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when requested by third party user")
    void getReservationById_OtherUser_ThrowsAccessDenied() {
        // Arrange
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationById(reservationId, otherEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para ver esta reserva");
    }

    // ----------------------------------------------------------------------
    // Tests para getReservationsByGuest
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should retrieve reservations for guest without status filter")
    void getReservationsByGuest_NoStatus_Success() {
        // Arrange
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation), pageable, 1);
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(reservationRepository.findByGuestIdAndDeletedFalse(guestId, pageable)).thenReturn(reservationPage);
        when(reservationMapper.toResponseDTO(any(Reservation.class))).thenReturn(responseDTO);

        // Act
        Page<ReservationResponseDTO> result = reservationService.getReservationsByGuest(guestEmail, null, pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findByGuestIdAndDeletedFalse(guestId, pageable);
        verify(reservationRepository, never()).findByGuestIdAndStatusAndDeletedFalse(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Should retrieve reservations for guest with status filter")
    void getReservationsByGuest_WithStatus_Success() {
        // Arrange
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation), pageable, 1);
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(reservationRepository.findByGuestIdAndStatusAndDeletedFalse(guestId, ReservationStatus.PENDING, pageable)).thenReturn(reservationPage);
        when(reservationMapper.toResponseDTO(any(Reservation.class))).thenReturn(responseDTO);

        // Act
        Page<ReservationResponseDTO> result = reservationService.getReservationsByGuest(guestEmail, "PENDING", pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findByGuestIdAndStatusAndDeletedFalse(guestId, ReservationStatus.PENDING, pageable);
    }

    // ----------------------------------------------------------------------
    // Tests para getReservationsByAccommodation
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should retrieve reservations for accommodation when requested by HOST")
    void getReservationsByAccommodation_Host_Success() {
        // Arrange
        Page<Reservation> reservationPage = new PageImpl<>(List.of(reservation), pageable, 1);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(reservationRepository.findByAccommodationIdAndDeletedFalse(accommodationId, pageable)).thenReturn(reservationPage);
        when(reservationMapper.toResponseDTO(any(Reservation.class))).thenReturn(responseDTO);

        // Act
        Page<ReservationResponseDTO> result = reservationService.getReservationsByAccommodation(accommodationId, hostEmail, null, pageable);

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(reservationRepository, times(1)).findByAccommodationIdAndDeletedFalse(accommodationId, pageable);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when requested by non-owner user")
    void getReservationsByAccommodation_NonOwner_ThrowsAccessDenied() {
        // Arrange
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationsByAccommodation(accommodationId, guestEmail, null, pageable))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para ver las reservas de este alojamiento");
        verify(reservationRepository, never()).findByAccommodationIdAndDeletedFalse(anyLong(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when invalid status is provided")
    void getReservationsByAccommodation_InvalidStatus_ThrowsIllegalArgument() {
        // Arrange
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationsByAccommodation(accommodationId, hostEmail, "INVALID_STATUS", pageable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estado de reserva inválido: INVALID_STATUS");
    }
}