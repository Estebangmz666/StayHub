package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TokenCleanupServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private TokenCleanupService tokenCleanupService;

    @Test
    @DisplayName("Should call repository to delete tokens using the current time as expiry date")
    void cleanupExpiredTokens_ShouldCallRepositoryWithCurrentTime() {
        // Arrange
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 10, 30);

        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // Act
            tokenCleanupService.cleanupExpiredTokens();

            // Assert
            verify(passwordResetTokenRepository, times(1))
                    .deleteByExpiryDateBefore(fixedTime);
        }
    }
}