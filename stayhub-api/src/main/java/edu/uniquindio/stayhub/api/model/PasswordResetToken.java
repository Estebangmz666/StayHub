    package edu.uniquindio.stayhub.api.model;

    import java.time.LocalDateTime;

    import jakarta.persistence.*;

    @Entity
    @Table(name = "password_reset_tokens", indexes = {
            @Index(name = "idx_token", columnList = "token")
    })
    public class PasswordResetToken {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String token;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(nullable = false)
        private LocalDateTime expiryDate;
    }