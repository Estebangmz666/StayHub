package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a token used for password reset functionality.
 * This token is linked to a specific user and includes an expiry date and a
 * status to prevent it from being used more than once.
 */
@Entity
@Table(name = "password_reset_tokens", indexes = {
        @Index(name = "idx_token", columnList = "token")
})
@Getter @Setter
public class PasswordResetToken {
    /**
     * The unique identifier for the password reset token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique, randomly generated token string. This is the value sent to the user.
     * It is indexed for fast lookup.
     */
    @Column(nullable = false)
    private String token;

    /**
     * The user associated with this password reset token.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The date and time when the token expires and is no longer valid.
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * A flag indicating whether the token has already been used to reset a password.
     * The default value is false.
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean used;
}