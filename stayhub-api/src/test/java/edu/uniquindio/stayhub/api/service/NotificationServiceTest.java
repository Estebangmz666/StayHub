package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationResponseDTO;
import edu.uniquindio.stayhub.api.dto.notification.NotificationUpdateDTO;
import edu.uniquindio.stayhub.api.exception.AccessDeniedException;
import edu.uniquindio.stayhub.api.exception.NotificationNotFoundException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.mapper.NotificationMapper;
import edu.uniquindio.stayhub.api.model.Notification;
import edu.uniquindio.stayhub.api.model.NotificationStatus;
import edu.uniquindio.stayhub.api.model.NotificationType;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.NotificationRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationMapper notificationMapper;
    @Mock private EmailService emailService;

    @InjectMocks private NotificationService notificationService;

    private User ownerUser;
    private User otherUser;
    private Notification notification;
    private NotificationRequestDTO requestDTO;
    private NotificationResponseDTO responseDTO;

    private final Long ownerId = 1L;
    private final Long notificationId = 100L;
    private final String ownerEmail = "owner@test.com";
    private final String otherEmail = "other@test.com";

    @BeforeEach
    void setup() {
        // 1. Usuarios
        ownerUser = new User();
        ownerUser.setId(ownerId);
        ownerUser.setEmail(ownerEmail);

        otherUser = new User();
        Long otherId = 2L;
        otherUser.setId(otherId);
        otherUser.setEmail(otherEmail);

        // 2. Notificación
        notification = new Notification();
        notification.setId(notificationId);
        notification.setUser(ownerUser);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setDeleted(false);
        notification.setMessage("Test message");

        // 3. DTOs
        requestDTO = new NotificationRequestDTO(ownerId, NotificationType.WELCOME, "Welcome message", NotificationStatus.UNREAD);
        responseDTO = new NotificationResponseDTO(notificationId, ownerId, NotificationType.WELCOME, "Welcome message", NotificationStatus.UNREAD, null);
    }


    @Test
    @DisplayName("Should create notification, save it, and send email successfully")
    void createNotification_Success(){
        // Arrange
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(ownerUser));
        when(notificationMapper.toEntity(requestDTO)).thenReturn(notification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        doNothing().when(emailService).sendEmailNotification(any(NotificationRequestDTO.class), anyString());

        // Act
        notificationService.createNotification(requestDTO);

        // Assert
        verify(userRepository, times(1)).findById(ownerId);
        verify(notificationRepository, times(1)).save(notification);
        verify(emailService, times(1)).sendEmailNotification(requestDTO, ownerEmail);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when creating notification for non-existent user")
    void createNotification_UserNotFound_ThrowsException() {
        // Arrange
        Long nonExistentId = 99L;
        NotificationRequestDTO invalidRequest = new NotificationRequestDTO(nonExistentId, NotificationType.WELCOME, "Msg", NotificationStatus.UNREAD);
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.createNotification(invalidRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuario con ID " + nonExistentId + " no encontrado");
        verify(notificationRepository, never()).save(any());
        verify(emailService, never()).sendEmailNotification(any(), anyString());
    }

    @Test
    @DisplayName("Should return all non-deleted notifications for the authenticated user (no status filter)")
    void getNotificationsByUser_NoStatusFilter_Success() {
        // Arrange
        List<Notification> notifications = List.of(notification);
        when(userRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(ownerUser));
        when(notificationRepository.findByUserIdAndDeletedFalse(ownerId)).thenReturn(notifications);
        when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByUser(ownerId, ownerEmail, null);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUserId()).isEqualTo(ownerId);
        verify(notificationRepository, times(1)).findByUserIdAndDeletedFalse(ownerId);
        verify(notificationRepository, never()).findByUserIdAndStatusAndDeletedFalse(anyLong(), any());
    }

    @Test
    @DisplayName("Should return filtered notifications when status is provided")
    void getNotificationsByUser_WithStatusFilter_Success() {
        // Arrange
        List<Notification> notifications = List.of(notification);
        when(userRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(ownerUser));
        when(notificationRepository.findByUserIdAndStatusAndDeletedFalse(ownerId, NotificationStatus.UNREAD)).thenReturn(notifications);
        when(notificationMapper.toResponseDTO(notification)).thenReturn(responseDTO);

        // Act
        List<NotificationResponseDTO> result = notificationService.getNotificationsByUser(ownerId, ownerEmail, "UNREAD");

        // Assert
        assertThat(result).hasSize(1);
        verify(notificationRepository, times(1)).findByUserIdAndStatusAndDeletedFalse(ownerId, NotificationStatus.UNREAD);
        verify(notificationRepository, never()).findByUserIdAndDeletedFalse(anyLong());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when an unauthorized user tries to view notifications")
    void getNotificationsByUser_UnauthorizedUser_ThrowsAccessDenied() {
        // Arrange
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.getNotificationsByUser(ownerId, otherEmail, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para ver las notificaciones de este usuario");
        verify(notificationRepository, never()).findByUserIdAndDeletedFalse(anyLong());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when an invalid status is provided")
    void getNotificationsByUser_InvalidStatus_ThrowsIllegalArgument() {
        // Arrange
        when(userRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(ownerUser));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.getNotificationsByUser(ownerId, ownerEmail, "INVALID_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estado de notificación inválido: INVALID_STATUS");
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if the target userId is not found")
    void getNotificationsByUser_TargetUserNotFound_ThrowsUserNotFound() {
        // Arrange - No stubs needed, the service will throw early

        // Act & Assert
        assertThatThrownBy(() -> notificationService.getNotificationsByUser(ownerId, ownerEmail, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("Should update notification status successfully when user is the owner")
    void updateNotificationStatus_Success() {
        // Arrange
        NotificationUpdateDTO updateDTO = new NotificationUpdateDTO(NotificationStatus.READ);
        Notification updatedNotification = new Notification();
        updatedNotification.setId(notificationId);
        updatedNotification.setUser(ownerUser);
        updatedNotification.setStatus(NotificationStatus.READ);

        NotificationResponseDTO updatedResponseDTO = new NotificationResponseDTO(notificationId, ownerId, NotificationType.WELCOME, "Msg", NotificationStatus.READ, null);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(userRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(ownerUser));
        when(notificationRepository.save(notification)).thenReturn(updatedNotification);
        when(notificationMapper.toResponseDTO(updatedNotification)).thenReturn(updatedResponseDTO);

        // Act
        NotificationResponseDTO result = notificationService.updateNotificationStatus(notificationId, updateDTO, ownerEmail);

        // Assert
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("Should throw NotificationNotFoundException when updating non-existent notification")
    void updateNotificationStatus_NotFound_ThrowsException() {
        // Arrange
        NotificationUpdateDTO updateDTO = new NotificationUpdateDTO(NotificationStatus.READ);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.updateNotificationStatus(notificationId, updateDTO, ownerEmail))
                .isInstanceOf(NotificationNotFoundException.class);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when unauthorized user tries to update")
    void updateNotificationStatus_UnauthorizedUser_ThrowsException() {
        // Arrange
        NotificationUpdateDTO updateDTO = new NotificationUpdateDTO(NotificationStatus.READ);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.updateNotificationStatus(notificationId, updateDTO, otherEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para update esta notificación");
        verify(notificationRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para deleteNotification
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should soft delete notification successfully when user is the owner")
    void deleteNotification_Success() {
        // Arrange
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(userRepository.findByEmail(ownerEmail)).thenReturn(Optional.of(ownerUser));

        // Act
        notificationService.deleteNotification(notificationId, ownerEmail);

        // Assert
        assertThat(notification.isDeleted()).isTrue();
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    @DisplayName("Should throw NotificationNotFoundException when deleting non-existent notification")
    void deleteNotification_NotFound_ThrowsException() {
        // Arrange
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, ownerEmail))
                .isInstanceOf(NotificationNotFoundException.class);
        verify(userRepository, never()).findByEmail(anyString());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when unauthorized user tries to delete")
    void deleteNotification_UnauthorizedUser_ThrowsException() {
        // Arrange
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(userRepository.findByEmail(otherEmail)).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThatThrownBy(() -> notificationService.deleteNotification(notificationId, otherEmail))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("No tienes permiso para delete esta notificación");
        verify(notificationRepository, never()).save(any());
    }
}