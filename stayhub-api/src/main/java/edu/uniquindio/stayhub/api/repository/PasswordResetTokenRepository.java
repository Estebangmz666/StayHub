package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing PasswordResetToken entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for specific data access needs related to password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    /**
     * Finds a password reset token by its unique token string.
     *
     * @param token The unique string of the token.
     * @return An Optional containing the found token, or an empty Optional if no token is found.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Deletes all password reset tokens associated with a specific user.
     *
     * @param user The user for whom to delete the tokens.
     */
    void deleteByUser(User user);

    /**
     * Finds a password reset token by its unique token string, but only if it has not been used.
     *
     * @param token The unique string of the token.
     * @return An Optional containing the found, unused token, or an empty Optional if no such token exists.
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    /**
     * Deletes all password reset tokens that expired before the specified date and time.
     *
     * @param datetime The date and time to compare against.
     */
    void deleteByExpiryDateBefore(LocalDateTime datetime);

    /**
     * Finds all unused password reset tokens for a specific user.
     *
     * @param user The user for whom to find the tokens.
     * @return A list of PasswordResetToken entities that are associated with the user and have not been used.
     */
    List<PasswordResetToken> findByUserAndUsedFalse(User user);

    /**
     * Counts the number of unused password reset tokens for a specific user.
     *
     * @param user The user for whom to count the tokens.
     * @return The count of unused tokens as a Long.
     */
    Long countByUserAndUsedFalse(User user);
}