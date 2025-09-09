package edu.uniquindio.stayhub.api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;

@Service
public class ReminderService {

    private final ReservationRepository reservationRepository;
    private final JavaMailSender mailSender;

    public ReminderService(ReservationRepository reservationRepository, JavaMailSender mailSender) {
        this.reservationRepository = reservationRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendCheckInReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Reservation> upcoming = reservationRepository.findByCheckInDate(tomorrow);
        for (Reservation reservation : upcoming) {
            sendEmail(reservation.getUser().getEmail(),
                    "Recordatorio de Check-In",
                    "Estimado/a " + reservation.getUser().getName() + ", le recordamos que su check-in para el alojamiento " +
                            reservation.getAccommodation().getTitle() + " está programado para el " + reservation.getCheckInDate() + ".");
            sendEmail(reservation.getAccommodation().getHost().getEmail(),
                    "Recordatorio de Huésped",
                    "Estimado/a " + reservation.getAccommodation().getHost().getName() +
                            ", un huésped llegará mañana a su alojamiento " + reservation.getAccommodation().getTitle() + ".");
        }
    }

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