package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    void deleteByExpiryDateBefore(LocalDateTime datetime);

    List<PasswordResetToken> findByUserAndUsedFalse(User user);

    Long countByUserAndUsedFalse(User user);
}