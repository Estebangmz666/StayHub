package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the StayHub application.
 * This class stores the user's personal and authentication information
 * and includes validation constraints to ensure data integrity.
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_role", columnList = "role"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends Auditable{

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's email address. It must be unique and in a valid format.
     */
    @Column(unique = true, nullable = false)
    @Email(message = "Formato de Email Invalido")
    private String email;

    /**
     * The user's password. It must meet complexity requirements: at least 8 characters,
     * including an uppercase letter and a number.
     */
    @Column(nullable = false)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas y números")
    private String password;

    /**
     * The user's role within the application (e.g., GUEST, HOST).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * The user's full name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The user's phone number, validated to match the Colombian format (+57 followed by 10 digits).
     */
    @Column(nullable = false)
    @Pattern(regexp = "^\\+57\\s?\\d{10}$", message = "El número de teléfono debe tener el formato colombiano: +57 seguido de 10 dígitos")
    private String phoneNumber;

    /**
     * The user's date of birth. It must be a date in the past.
     */
    @Column(nullable = false)
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    /**
     * A flag to indicate if the user has been soft-deleted.
     * Defaults to false.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    /**
     * An optional URL for the user's profile picture.
     */
    @URL(message = "Debe ser una URL válida")
    @Column
    private String profilePicture;

    /**
     * The one-to-one relationship with the HostProfile, applicable if the user is a host.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "host_profile_id")
    private HostProfile hostProfile;

    /**
     * A list of accommodations owned by this user (as a host).
     */
    @OneToMany(mappedBy = "host")
    @Builder.Default
    private List<Accommodation> accommodations = new ArrayList<>();

    /**
     * A list of reservations made by this user (as a guest).
     */
    @Builder.Default
    @OneToMany(mappedBy = "guest")
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * A list of comments made by this user.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    public User(String name, String email){
        this.name = name;
        this.email = email;
    }
}