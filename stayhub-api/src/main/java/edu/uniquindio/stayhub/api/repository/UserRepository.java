package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities, providing methods for
 * data access and persistence.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User entity by their email address.
     *
     * @param email The email address to search for.
     * @return An Optional containing the found User, or an empty Optional if no user is found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all User entities that are not marked as deleted.
     *
     * @return A list of non-deleted User entities.
     */
    List<User> findByDeletedFalse();

    /**
     * Checks if a User entity exists with the given email address.
     *
     * @param email The email address to check for existence.
     * @return true if a user with the email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}