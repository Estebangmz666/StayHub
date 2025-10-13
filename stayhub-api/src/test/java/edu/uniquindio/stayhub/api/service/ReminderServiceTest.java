package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.Reservation;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private JavaMailSender mailSender;

    // Usamos @Spy y @InjectMocks para poder interceptar la llamada al método privado sendEmail
    @Spy
    @InjectMocks
    private ReminderService reminderService;

    private Reservation mockReservation;
    private User host;
    private LocalDateTime tomorrow;

    @BeforeEach
    void setUp() {
        // Simular 'mañana' (la fecha de búsqueda) para el test
        tomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 2. Usuarios
        User guest = new User();
        guest.setName("Laura");
        guest.setEmail("laura@guest.com");

        host = new User();
        host.setName("Rafael");
        host.setEmail("rafael@host.com");

        // 3. Alojamiento
        Accommodation accommodation = new Accommodation();
        accommodation.setTitle("Villa Hermosa");
        accommodation.setHost(host);

        // 4. Reserva
        mockReservation = new Reservation();
        mockReservation.setGuest(guest);
        mockReservation.setAccommodation(accommodation);
        // Aunque el servicio busca con la fecha actual + 1, solo nos interesa
        // que el repositorio devuelva esta reserva.
        mockReservation.setCheckInDate(tomorrow);
    }

    // ----------------------------------------------------------------------
    // Tests para sendCheckInReminders (La tarea programada)
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should send two emails (guest and host) for one upcoming reservation")
    void sendCheckInReminders_OneReservation_SendsTwoEmails() {
        // Arrange
        doNothing().when(reminderService).sendEmail(anyString(), anyString(), anyString());

        List<Reservation> upcomingReservations = List.of(mockReservation);

        // Mockear la respuesta del repositorio. Usamos any() porque la fecha exacta de 'tomorrow'
        // puede variar ligeramente en la hora/minuto/segundo al ejecutar, aunque el día será el mismo.
        // Si el repositorio usa solo el componente de fecha, sería más fácil.
        // Para este caso, Mockito.any() es seguro para verificar la invocación.
        when(reservationRepository.findByCheckInDate(any(LocalDateTime.class))).thenReturn(upcomingReservations);

        // Act
        reminderService.sendCheckInReminders();

        // Assert
        // Se espera que el repositorio sea llamado una vez con la fecha de mañana.
        verify(reservationRepository, times(1)).findByCheckInDate(any(LocalDateTime.class));

        // Se espera que el método sendEmail sea llamado dos veces (una para el huésped, otra para el anfitrión).
        verify(reminderService, times(2)).sendEmail(anyString(), anyString(), anyString());

        // Verificación de los argumentos del correo al Huésped (1er llamado)
        verify(reminderService, times(1)).sendEmail(
                Mockito.eq("laura@guest.com"),
                Mockito.eq("Recordatorio de Check-In"),
                Mockito.contains("Laura, le recordamos que su check-in para el alojamiento Villa Hermosa")
        );

        // Verificación de los argumentos del correo al Anfitrión (2.º llamado)
        verify(reminderService, times(1)).sendEmail(
                Mockito.eq("rafael@host.com"),
                Mockito.eq("Recordatorio de Huésped"),
                Mockito.contains("Rafael, un huésped llegará mañana a su alojamiento Villa Hermosa")
        );
    }

    @Test
    @DisplayName("Should send four emails for two upcoming reservations")
    void sendCheckInReminders_TwoReservations_SendsFourEmails() {
        // Arrange
        doNothing().when(reminderService).sendEmail(anyString(), anyString(), anyString());

        Reservation secondReservation = new Reservation();
        secondReservation.setGuest(new User("Carlos", "carlos@guest.com"));
        secondReservation.setAccommodation(new Accommodation("Apartamento Central", host));
        secondReservation.setCheckInDate(tomorrow);

        List<Reservation> upcomingReservations = List.of(mockReservation, secondReservation);

        when(reservationRepository.findByCheckInDate(any(LocalDateTime.class))).thenReturn(upcomingReservations);

        // Act
        reminderService.sendCheckInReminders();

        // Assert
        // El ciclo debe iterar dos veces, llamando a sendEmail dos veces en cada iteración.
        verify(reminderService, times(4)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should not send any email if no upcoming reservations are found")
    void sendCheckInReminders_NoReservations_SendsZeroEmails() {
        // Arrange
        // Don't stub sendEmail here since it won't be called

        when(reservationRepository.findByCheckInDate(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // Act
        reminderService.sendCheckInReminders();

        // Assert
        verify(reservationRepository, times(1)).findByCheckInDate(any(LocalDateTime.class));
        verify(reminderService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    // ----------------------------------------------------------------------
    // Tests para sendEmail (Verificación del manejo de excepciones)
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("sendEmail should handle MailException gracefully without stopping the application")
    void sendEmail_MailException_IsCaught() {
        // Arrange
        // Re-stubbing el método privado para simular un fallo en el envío del correo real
        // Necesitamos deshabilitar el doNothing() del @BeforeEach primero.
        Mockito.reset(reminderService);

        // El método privado 'sendEmail' llama a mailSender.send(message)
        doThrow(new MailException("Simulated mail failure") {}).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        // El test pasa si el método se ejecuta sin lanzar una excepción (la excepción se captura internamente)
        reminderService.sendEmail("test@fail.com", "Test", "Body");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}