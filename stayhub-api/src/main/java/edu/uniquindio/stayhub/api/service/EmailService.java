package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.notification.NotificationRequestDTO;
import edu.uniquindio.stayhub.api.model.NotificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    /**
     * Sends a password-reset email to the specified email address with a reset token.
     *
     * @param email The recipient's email address.
     * @param token The password reset token.
     */
    public void sendPasswordResetEmail(String email, String token) {
        LOGGER.info("Sending password reset email to: {}", email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("StayHub - Restablecer Contraseña");
        message.setText("Hola,\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña en StayHub.\n" +
                "Haz clic en el siguiente enlace para restablecer tu contraseña:\n" +
                "https://localhost:3030" + token + "\n\n" +  //TODO replace for frontend password reset url
                "Este enlace expira en 15 minutos. Si no solicitaste este cambio, ignora este correo.\n\n" +
                "¡Gracias por usar StayHub!");
        mailSender.send(message);
        LOGGER.debug("Password reset email sent successfully to: {}", email);
    }

    /**
     * Sends an email notification based on the provided NotificationRequestDTO.
     *
     * @param notificationDTO The notification details.
     * @param recipientEmail The recipient's email address.
     * @throws MessagingException If there is an error sending the email.
     */
    public void sendEmailNotification(NotificationRequestDTO notificationDTO, String recipientEmail) throws MessagingException {
        LOGGER.info("Sending email notification to: {}", recipientEmail);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipientEmail);
        helper.setSubject(getEmailSubject(notificationDTO.getNotificationType()));
        helper.setText(buildEmailContent(notificationDTO), true);

        mailSender.send(message);
        LOGGER.debug("Email sent successfully to: {}", recipientEmail);
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
            case WELCOME -> "¡Bienvenido a StayHub!";
            default -> "Notificación de StayHub";
        };
    }

    /**
     * Builds the email content based on the notification details.
     *
     * @param notificationDTO The notification details.
     * @return The HTML content for the email.
     */
    private String buildEmailContent(NotificationRequestDTO notificationDTO) {
        return """
            <html>
                <body>
                    <h2>Notificación de StayHub</h2>
                    <p>%s</p>
                    <p>Gracias por usar StayHub.</p>
                </body>
            </html>
            """.formatted(notificationDTO.getMessage());
    }
}