package edu.uniquindio.stayhub.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;

/**
 * Service for sending automated email reminders related to upcoming reservations.
 * This service uses a scheduled task to check for reservations that require reminders.
 */
@Service
public class ReminderService {

    private final ReservationRepository reservationRepository;
    private final JavaMailSender mailSender;

    /**
     * Constructs the ReminderService with necessary dependencies.
     *
     * @param reservationRepository The repository to access reservation data.
     * @param mailSender The mail sender for sending emails.
     */
    public ReminderService(ReservationRepository reservationRepository, JavaMailSender mailSender) {
        this.reservationRepository = reservationRepository;
        this.mailSender = mailSender;
    }

    /**
     * A scheduled task that runs every day at 8 AM to send check-in reminders.
     * It finds all reservations with a check-in date for the following day and
     * sends a reminder email to both the guest and the host.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendCheckInReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Reservation> upcoming = reservationRepository.findByCheckInDate(tomorrow);
        for (Reservation reservation : upcoming) {
            sendEmail(reservation.getGuest().getEmail(),
                    "Recordatorio de Check-In",
                    "Estimado/a " + reservation.getGuest().getName() + ", le recordamos que su check-in para el alojamiento " +
                            reservation.getAccommodation().getTitle() + " está programado para el " + reservation.getCheckInDate() + ".");
            sendEmail(reservation.getAccommodation().getHost().getEmail(),
                    "Recordatorio de Huésped",
                    "Estimado/a " + reservation.getAccommodation().getHost().getName() +
                            ", un huésped llegará mañana a su alojamiento " + reservation.getAccommodation().getTitle() + ".");
        }
    }

    /**
     * Sends a simple email message to a specified recipient.
     *
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param body The body content of the email.
     */
    private void sendEmail(String to, String subject, String body){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Error enviando correo a " + to + ": " + e.getMessage());
        }
    }
}