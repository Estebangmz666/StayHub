package edu.uniquindio.stayhub.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a comment left by a user on accommodation.
 * This class maps to the 'comments' table in the database and includes
 * details about the user, the accommodation, the comment text, and a rating.
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_accommodation_id", columnList = "accommodation_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {

    /**
     * The unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who wrote the comment.
     * This is a many-to-one relationship with the User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The accommodation to which the comment belongs.
     * This is a many-to-one relationship with the Accommodation entity.
     */
    @ManyToOne
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    /**
     * The text content of the comment.
     */
    @Column(nullable = false, length = 500)
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String text;

    /**
     * The numerical rating given by the user, from 1 to 5.
     */
    @Column(nullable = false)
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación debe ser entre 1 y 5")
    @Max(value = 5, message = "La calificación debe ser entre 1 y 5")
    private Integer rating;

    /**
     * The timestamp when the comment was created.
     */
    @Column(nullable = false)
    @NotNull(message = "La fecha del comentario es obligatoria")
    @PastOrPresent(message = "La fecha del comentario debe ser en el pasado o presente")
    private LocalDateTime createdAt;

    /**
     * A flag indicating whether the comment has been soft-deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean deleted = false;

    /**
     * Lifecycle method that sets the creation timestamp and the deleted flag
     * to their default values before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }
}