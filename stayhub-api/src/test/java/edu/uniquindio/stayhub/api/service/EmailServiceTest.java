package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.model.NotificationType;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

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
        // Inyectar los campos @Value simulados
        ReflectionTestUtils.setField(emailService, "frontendUrl", FRONTEND_URL);
        ReflectionTestUtils.setField(emailService, "defaultFrom", "noreply@stayhub.com");

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Configure TemplateEngine to return mock HTML content
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenReturn("<html><body>Mock email content</body></html>");
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
        // Verify and capture the arguments in one call
        verify(emailService, times(1)).sendEmailWithTemplate(
                toCaptor.capture(),
                subjectCaptor.capture(),
                templateNameCaptor.capture(),
                variablesCaptor.capture()
        );

        // Verify the captured arguments
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

        // Assert: capturar los argumentos
        verify(emailService).sendEmailWithTemplate(
                toCaptor.capture(),
                subjectCaptor.capture(),
                templateNameCaptor.capture(),
                variablesCaptor.capture()
        );

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
                NotificationType.ACCOMMODATION_CREATED, // no está en switch
                message,
                null
        );

        // Act
        emailService.sendEmailNotification(notificationDTO, recipientEmail);

        // Assert
        // Capturamos los argumentos reales usados en el método interno
        verify(emailService, times(1)).sendEmailWithTemplate(
                toCaptor.capture(),
                subjectCaptor.capture(),
                templateNameCaptor.capture(),
                variablesCaptor.capture()
        );

        // Ahora sí comparamos
        assertThat(toCaptor.getValue()).isEqualTo(recipientEmail);
        assertThat(subjectCaptor.getValue()).isEqualTo("Notificación de StayHub"); // default
        assertThat(templateNameCaptor.getValue()).isEqualTo("emails/notification"); // default

        Map<String, Object> variables = variablesCaptor.getValue();
        assertThat(variables.get("message")).isEqualTo(message);
    }
}