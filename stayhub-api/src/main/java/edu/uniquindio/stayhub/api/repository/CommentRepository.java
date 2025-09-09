package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByAccommodationIdAndIsDeletedFalseOrderByCreatedAtDesc(Long accommodationId);

    Page<Comment> findByAccommodationIdAndIsDeletedFalseOrderByCreatedAtDesc(Long accommodationId, Pageable pageable);

    List<Comment> findByUserIdAndIsDeletedFalse(Long userId);

    boolean existsByUserIdAndAccommodationIdAndIsDeletedFalse(Long userId, Long accommodationId);

    @Query("SELECT AVG(c.rating) FROM Comment c WHERE c.accommodation.id = :accommodationId AND c.isDeleted = false")
    Double findAverageRatingByAccommodationId(@Param("accommodationId") Long accommodationId);

    long countByAccommodationIdAndIsDeletedFalse(Long accommodationId);
}