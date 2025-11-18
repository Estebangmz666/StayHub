package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.model.NotificationType;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.mail.from}")
    private String defaultFrom;

    /**
     * Sends an email using a Thymeleaf template.
     *
     * @param to The recipient's email address.
     * @param subject The email subject.
     * @param templateName The name of the template (without .html extension).
     * @param variables Variables to pass to the template.
     */
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(defaultFrom);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            LOGGER.info("Email sent successfully to: {} using template: {}", to, templateName);
        } catch (Exception e) {
            LOGGER.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Sends a password-reset email to the specified email address with a reset token.
     *
     * @param email The recipient's email address.
     * @param token The password reset token.
     */
    @Async
    public void sendPasswordResetEmail(String email, String token, String firstName) {
        String link = frontendUrl + "/reset-password?token=" + token;

        // Preparar variables para la plantilla
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetLink", link);
        variables.put("expirationMinutes", 15);
        variables.put("token", token);
        variables.put("firstName", firstName);

        sendEmailWithTemplate(
                email,
                "StayHub - Restablecer Contraseña",
                "emails/password-reset",
                variables
        );
    }

    /**
     * Sends an email notification based on the provided NotificationRequestDTO.
     *
     * @param notificationDTO The notification details.
     * @param recipientEmail The recipient's email address.
     */
    @Async
    public void sendEmailNotification(NotificationRequestDTO notificationDTO, String recipientEmail) {
        String subject = getEmailSubject(notificationDTO.getNotificationType());
        String templateName = getTemplateName(notificationDTO.getNotificationType());

        // Preparar variables para la plantilla
        Map<String, Object> variables = new HashMap<>();
        variables.put("message", notificationDTO.getMessage());
        variables.put("notificationType", notificationDTO.getNotificationType().name());

        sendEmailWithTemplate(recipientEmail, subject, templateName, variables);
    }

    /**
     * Determines the email subject based on the notification type.
     *
     * @param type The notification type.
     * @return The email subject.
     */
    private String getEmailSubject(NotificationType type) {
        return switch (type) {
            case RESERVATION_CANCELLED -> "Reserva Cancelada en StayHub";
            case RESERVATION_CONFIRMED -> "Confirmación de Reserva en StayHub";
            case RESERVATION_UPDATED -> "Actualización de Reserva en StayHub";
            case RESERVATION_REQUESTED -> "Nueva Solicitud de Reserva en StayHub";
            case RESERVATION_CREATED -> "Tu Reserva en StayHub";
            case WELCOME -> "¡Bienvenido a StayHub!";
            default -> "Notificación de StayHub";
        };
    }

    /**
     * Determines the template name based on the notification type.
     *
     * @param type The notification type.
     * @return The template name (without .html extension).
     */
    private String getTemplateName(NotificationType type) {
        return switch (type) {
            case RESERVATION_CANCELLED -> "emails/reservation-cancelled";
            case RESERVATION_CONFIRMED -> "emails/reservation-confirmed";
            case RESERVATION_UPDATED -> "emails/reservation-updated";
            case RESERVATION_REQUESTED -> "emails/reservation-requested";
            case RESERVATION_CREATED -> "emails/reservation-created";
            case WELCOME -> "emails/welcome";
            default -> "emails/notification";
        };
    }
}