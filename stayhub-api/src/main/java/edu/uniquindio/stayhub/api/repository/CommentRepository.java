package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Comment entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for specific data access needs.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Retrieves a list of non-deleted comments for a specific accommodation,
     * ordered by creation date in descending order.
     *
     * @param accommodationId The ID of the accommodation.
     * @return A list of Comment entities.
     */
    List<Comment> findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(Long accommodationId);

    /**
     * Retrieves a paginated list of non-deleted comments for a specific accommodation,
     * ordered by creation date in descending order.
     *
     * @param accommodationId The ID of the accommodation.
     * @param pageable The pagination information.
     * @return A Page of Comment entities.
     */
    Page<Comment> findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    /**
     * Finds all non-deleted comments made by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Comment entities.
     */
    List<Comment> findByUserIdAndDeletedFalse(Long userId);

    /**
     * Checks if a non-deleted comment exists from a specific user for a given accommodation.
     *
     * @param userId The ID of the user.
     * @param accommodationId The ID of the accommodation.
     * @return true if a comment exists, false otherwise.
     */
    boolean existsByUserIdAndAccommodationIdAndDeletedFalse(Long userId, Long accommodationId);

    /**
     * Calculates the average rating of all non-deleted comments for a specific accommodation.
     *
     * @param accommodationId The ID of the accommodation.
     * @return The average rating as a Double.
     */
    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.accommodation.id = :accommodationId AND c.deleted = false")
    Double findAverageRatingByAccommodationId(@Param("accommodationId") Long accommodationId);

    /**
     * Counts the number of non-deleted comments for a specific accommodation.
     *
     * @param accommodationId The ID of the accommodation.
     * @return The count of non-deleted comments.
     */
    long countByAccommodationIdAndDeletedFalse(Long accommodationId);
}