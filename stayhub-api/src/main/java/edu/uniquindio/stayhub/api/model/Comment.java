package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_accommodation_id", columnList = "accommodation_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String text;

    @Column(nullable = false)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación debe ser entre 1 y 5")
    @Max(value = 5, message = "La calificación debe ser entre 1 y 5")
    private Integer rating;

    @Column(nullable = false)
    @NotNull(message = "La fecha del comentario es obligatoria")
    @PastOrPresent(message = "La fecha del comentario debe ser en el pasado o presente")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.isDeleted = false;
    }
}