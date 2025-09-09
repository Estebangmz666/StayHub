package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.Comment;
import edu.uniquindio.stayhub.api.repository.CommentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CommentDAO {
    private final CommentRepository commentRepository;

    public CommentDAO(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        if (comment.getRating() < 1 || comment.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return commentRepository.save(comment);
    }

    public Optional<Comment> findActiveById(Long id) {
        return commentRepository.findById(id).filter(c -> !c.isDeleted());
    }

    public List<Comment> findActiveByAccommodationId(Long accommodationId) {
        return commentRepository.findByAccommodationIdAndIsDeletedFalseOrderByCreatedAtDesc(accommodationId);
    }
}