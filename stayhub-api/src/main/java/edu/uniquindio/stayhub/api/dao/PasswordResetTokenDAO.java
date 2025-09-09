package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PasswordResetTokenDAO {
    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetTokenDAO(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public PasswordResetToken saveToken(PasswordResetToken token) {
        if (tokenRepository.countByUserAndUsedFalse(token.getUser()) >= 3) {
            throw new IllegalStateException("Maximum number of active tokens reached");
        }
        return tokenRepository.save(token);
    }

    public void cleanupExpiredTokens() {
        tokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    public List<PasswordResetToken> findActiveByUser(User user) {
        return tokenRepository.findByUserAndUsedFalse(user);
    }
}