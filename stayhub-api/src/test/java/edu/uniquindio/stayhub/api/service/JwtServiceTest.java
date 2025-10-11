package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    // Clave secreta fuerte para pruebas
    private static final String TEST_SECRET = "this-is-a-very-long-and-secure-secret-key-for-testing-purposes-1234567890";

    // Tiempo de expiración normal (1 hora)
    private static final long EXPIRATION_TIME_NORMAL = TimeUnit.HOURS.toMillis(1);

    // Tiempo de expiración corto (para forzar el vencimiento)
    private static final long EXPIRATION_TIME_SHORT = 10; // 10 milisegundos

    private JwtService jwtService;
    private User testUser;
    private UserDetails testUserDetails;
    private SecretKey secretKey; // Clave secreta para firmar/verificar tokens

    @BeforeEach
    void setUp() {
        // Generar la SecretKey a partir de TEST_SECRET
        secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));

        // Inicializa el servicio con la clave y tiempo de expiración normales
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_TIME_NORMAL);

        // Configuración de User
        testUser = new User();
        testUser.setId(10L);
        testUser.setEmail("test@stayhub.com");
        testUser.setRole(Role.HOST);

        // Configuración de UserDetails para Spring Security
        testUserDetails = new UserDetails() {
            @Override
            public String getUsername() { return "detailuser@stayhub.com"; }
            @Override
            public String getPassword() { return "password"; }
            @Override
            public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"));
            }
            @Override
            public boolean isAccountNonExpired() { return true; }
            @Override
            public boolean isAccountNonLocked() { return true; }
            @Override
            public boolean isCredentialsNonExpired() { return true; }
            @Override
            public boolean isEnabled() { return true; }
        };
    }

    // ----------------------------------------------------------------------
    // Tests para generateToken(User)
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should generate a token with correct subject, userId, and role claims")
    void generateToken_User_CorrectClaims() {
        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        assertDoesNotThrow(() -> {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verificar claims
            assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
            assertThat(claims.get("userId", Long.class)).isEqualTo(testUser.getId());
            assertThat(claims.get("role", String.class)).isEqualTo(testUser.getRole().name());

            // Verificar tiempo de expiración
            long expectedExpiration = System.currentTimeMillis() + EXPIRATION_TIME_NORMAL;
            assertThat(claims.getExpiration().getTime()).isCloseTo(expectedExpiration, Percentage.withPercentage(1000)); // Tolerancia de 1s
        });
    }

    // ----------------------------------------------------------------------
    // Tests para generateToken(UserDetails)
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should generate a token with correct subject and role claims from UserDetails")
    void generateToken_UserDetails_CorrectClaims() {
        // Act
        String token = jwtService.generateToken(testUserDetails);

        // Assert
        assertDoesNotThrow(() -> {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verificar claims
            assertThat(claims.getSubject()).isEqualTo(testUserDetails.getUsername());
            assertThat(claims.get("role", String.class)).isEqualTo("ROLE_GUEST");
            // No debería tener claim "userId"
            assertThat(claims.containsKey("userId")).isFalse();
        });
    }

    // ----------------------------------------------------------------------
    // Tests para validateToken
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should validate a properly signed and non-expired token")
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act & Assert
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when token is expired")
    void validateToken_ExpiredToken_ThrowsException() throws InterruptedException {
        // Arrange: Inicializa un nuevo servicio con tiempo de expiración corto
        JwtService shortLivedService = new JwtService(TEST_SECRET, EXPIRATION_TIME_SHORT);
        String expiredToken = shortLivedService.generateToken(testUser);

        // Esperar a que el token expire (un poco más de 10 ms)
        Thread.sleep(100);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Should throw JwtException when token is signed with a different secret")
    void validateToken_InvalidSignature_ThrowsException() {
        // Arrange: Generar un token con una clave diferente
        SecretKey differentKey = Keys.hmacShaKeyFor("different-secret-key-for-testing".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject(testUser.getEmail()) // Usar subject() en lugar de setSubject()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_NORMAL))
                .signWith(differentKey)
                .compact();

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.validateToken(token));
    }

    @Test
    @DisplayName("Should throw JwtException when token is malformed")
    void validateToken_MalformedToken_ThrowsException() {
        // Arrange
        String malformedToken = "invalid.token.structure";

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.validateToken(malformedToken));
    }

    // ----------------------------------------------------------------------
    // Tests para extractUsername
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should correctly extract username (email) from a valid token")
    void extractUsername_ValidToken_ReturnsEmail() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when extracting username from expired token")
    void extractUsername_ExpiredToken_ThrowsException() throws InterruptedException {
        // Arrange
        JwtService shortLivedService = new JwtService(TEST_SECRET, EXPIRATION_TIME_SHORT);
        String expiredToken = shortLivedService.generateToken(testUser);
        Thread.sleep(100);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(expiredToken));
    }

    // ----------------------------------------------------------------------
    // Tests para extractUserId
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should correctly extract userId from a token generated from User object")
    void extractUserId_UserToken_ReturnsUserId() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        Long userId = jwtService.extractUserId(token);

        // Assert
        assertThat(userId).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should return null when extracting userId from a token generated from UserDetails (no userId claim)")
    void extractUserId_UserDetailsToken_ReturnsNull() {
        // Arrange
        String token = jwtService.generateToken(testUserDetails);

        // Act
        Long userId = jwtService.extractUserId(token);

        // Assert
        assertThat(userId).isNull();
    }

    // ----------------------------------------------------------------------
    // Tests para isTokenValid
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("isTokenValid should return true for a valid token matching the user details")
    void isTokenValid_MatchesUser_ReturnsTrue() {
        // Arrange
        // Crear UserDetails que coincida con el token
        UserDetails matchingUserDetails = new org.springframework.security.core.userdetails
                .User(
                testUser.getEmail(),
                "any-password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name()))
        );
        String token = jwtService.generateToken(testUser);

        // Act & Assert
        assertTrue(jwtService.isTokenValid(token, matchingUserDetails));
    }

    @Test
    @DisplayName("isTokenValid should return false if username does not match")
    void isTokenValid_MismatchedUser_ReturnsFalse() {
        // Arrange
        // Token generado para test@stayhub.com
        String token = jwtService.generateToken(testUser);

        // UserDetails de otro usuario
        UserDetails differentUser = new org.springframework.security.core.userdetails.User(
                "other@stayhub.com",
                "any-password",
                Collections.emptyList()
        );

        // Act & Assert
        assertFalse(jwtService.isTokenValid(token, differentUser));
    }

    @Test
    @DisplayName("isTokenValid should return false if token is expired")
    void isTokenValid_ExpiredToken_ReturnsFalse() throws InterruptedException {
        // Arrange
        JwtService shortLivedService = new JwtService(TEST_SECRET, EXPIRATION_TIME_SHORT);
        String expiredToken = shortLivedService.generateToken(testUser);
        Thread.sleep(100); // Espera a la expiración

        // Act & Assert
        assertFalse(jwtService.isTokenValid(expiredToken, testUserDetails));
    }
}