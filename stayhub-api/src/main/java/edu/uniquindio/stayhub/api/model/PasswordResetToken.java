    package edu.uniquindio.stayhub.api.model;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "password_reset_tokens", indexes = {
            @Index(name = "idx_token", columnList = "token")
    })
    @Getter @Setter
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

        @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
        private boolean used;
    }