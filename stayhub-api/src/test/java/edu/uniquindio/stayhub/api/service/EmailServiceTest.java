package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.model.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {



    // Usamos Spy para interceptar la llamada al método privado sendEmailWithTemplate
    @Spy
    @InjectMocks
    private EmailService emailService;

    // Captors para verificar los argumentos pasados al método privado
    @Captor
    private ArgumentCaptor<String> toCaptor;
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    @Captor
    private ArgumentCaptor<String> templateNameCaptor;
    @Captor
    private ArgumentCaptor<Map<String, Object>> variablesCaptor;

    private final String FRONTEND_URL = "http://localhost:4200";

    @BeforeEach
    void setUp() {
        // Inyectar los campos @Value usando ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "frontendUrl", FRONTEND_URL);
        String DEFAULT_FROM = "noreply@stayhub.com";
        ReflectionTestUtils.setField(emailService, "defaultFrom", DEFAULT_FROM);

        // Usar doNothing para evitar la ejecución real del método sendEmailWithTemplate,
        // pero capturar sus argumentos para la verificación.
        // Nota: Esta técnica verifica la invocación de un método dentro de un Spy.
        try {
            Mockito.doAnswer(invocation -> {
                toCaptor.capture();
                subjectCaptor.capture();
                templateNameCaptor.capture();
                variablesCaptor.capture();
                return null;
            }).when(emailService).sendEmailWithTemplate(toCaptor.capture(), subjectCaptor.capture(), templateNameCaptor.capture(), variablesCaptor.capture());
        } catch (Exception e) {
            // Manejar la excepción de stubbing, si es necesario, aunque el doAnswer/when debería funcionar
        }
    }

    // ----------------------------------------------------------------------
    // Tests para sendPasswordResetEmail
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should prepare and send email with correct link and template for password reset")
    void sendPasswordResetEmail_Success() {
        // Arrange
        String email = "user@test.com";
        String token = "reset-token-123";
        String expectedLink = FRONTEND_URL + "/reset-password?token=" + token;

        // Act
        emailService.sendPasswordResetEmail(email, token);

        // Assert
        // 1. Verificación de la llamada al método interno
        verify(emailService, times(1)).sendEmailWithTemplate(anyString(), anyString(), anyString(), anyMap());

        // 2. Verificación de los argumentos capturados
        assertThat(toCaptor.getValue()).isEqualTo(email);
        assertThat(subjectCaptor.getValue()).isEqualTo("StayHub - Restablecer Contraseña");
        assertThat(templateNameCaptor.getValue()).isEqualTo("emails/password-reset");

        Map<String, Object> variables = variablesCaptor.getValue();
        assertThat(variables).containsKeys("resetLink", "expirationMinutes", "token");
        assertThat(variables.get("resetLink")).isEqualTo(expectedLink);
        assertThat(variables.get("token")).isEqualTo(token);
    }

    // ----------------------------------------------------------------------
    // Tests para sendEmailNotification
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should prepare and send email with correct subject/template for RESERVATION_CONFIRMED")
    void sendEmailNotification_ReservationConfirmed_Success() {
        // Arrange
        String recipientEmail = "host@test.com";
        String message = "Your booking has been confirmed.";
        NotificationRequestDTO notificationDTO = new NotificationRequestDTO(
                2L,
                NotificationType.RESERVATION_CONFIRMED,
                message,
                null
        );

        // Act
        emailService.sendEmailNotification(notificationDTO, recipientEmail);

        // Assert
        // 1. Verificación de la llamada
        verify(emailService, times(1)).sendEmailWithTemplate(anyString(), anyString(), anyString(), anyMap());

        // 2. Verificación de los argumentos (Subject y TemplateName dependen del tipo)
        assertThat(toCaptor.getValue()).isEqualTo(recipientEmail);
        assertThat(subjectCaptor.getValue()).isEqualTo("Confirmación de Reserva en StayHub");
        assertThat(templateNameCaptor.getValue()).isEqualTo("emails/reservation-confirmed");

        Map<String, Object> variables = variablesCaptor.getValue();
        assertThat(variables).containsKeys("message", "notificationType");
        assertThat(variables.get("message")).isEqualTo(message);
    }

    @Test
    @DisplayName("Should use default subject and template for types not explicitly listed")
    void sendEmailNotification_UnknownType_ShouldUseDefault() {
        // Arrange
        String recipientEmail = "user@test.com";
        String message = "A general update message.";
        NotificationRequestDTO notificationDTO = new NotificationRequestDTO(
                1L,
                NotificationType.ACCOMMODATION_CREATED, // No está en los switch case
                message,
                null
        );

        // Act
        emailService.sendEmailNotification(notificationDTO, recipientEmail);

        // Assert
        // 1. Verificación de la llamada
        verify(emailService, times(1)).sendEmailWithTemplate(anyString(), anyString(), anyString(), anyMap());

        // 2. Verificación de los argumentos (Debe usar los valores por defecto)
        assertThat(toCaptor.getValue()).isEqualTo(recipientEmail);
        assertThat(subjectCaptor.getValue()).isEqualTo("Notificación de StayHub"); // Subject por defecto
        assertThat(templateNameCaptor.getValue()).isEqualTo("emails/notification"); // Template por defecto

        Map<String, Object> variables = variablesCaptor.getValue();
        assertThat(variables.get("message")).isEqualTo(message);
    }
}