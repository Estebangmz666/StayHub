package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class TokenCleanupService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens(){
        passwordResetTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}