package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Service class for handling JWT token generation and validation in the StayHub application.
 * Generates tokens with user email and role, and validates tokens using a secret key loaded from environment variables.
 */
@Service
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private final Key SECRET_KEY;
    private final long EXPIRATION_TIME;

    /**
     * Constructs a JwtService with the secret key and expiration time loaded from environment variables.
     *
     * @param secretKey The secret key for signing JWT tokens, loaded from application properties.
     * @param expirationTime The token expiration time in milliseconds, loaded from application properties.
     */
    public JwtService(@Value("${jwt.secret.key}") String secretKey, @Value("${jwt.time.expiration:86400000}") long expirationTime) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.EXPIRATION_TIME = expirationTime;
    }

    /**
     * Generates a JWT token for a user, including their email as the subject and role as a claim.
     *
     * @param user The user for whom the token is generated.
     * @return The generated JWT token as a string.
     */
    public String generateToken(User user) {
        LOGGER.info("Generating JWT token for user: {}", user.getEmail());
        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        LOGGER.debug("JWT token generated for user: {}", user.getEmail());
        return token;
    }

    /**
     * Generates a JWT token for a UserDetails object, compatible with Spring Security.
     *
     * @param userDetails The user details for whom the token is generated.
     * @return The generated JWT token as a string.
     */
    public String generateToken(UserDetails userDetails) {
        LOGGER.info("Generating JWT token for user details: {}", userDetails.getUsername());
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        LOGGER.debug("JWT token generated for user details: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Validates a JWT token by checking its signature and expiration.
     *
     * @param token The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     * @throws ExpiredJwtException If the token has expired.
     * @throws JwtException If the token is invalid (e.g., malformed or incorrect signature).
     */
    public boolean validateToken(String token) {
        LOGGER.info("Validating JWT token");
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);
            LOGGER.debug("JWT token validated successfully");
            return true;
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Extracts the username (email) from a JWT token.
     *
     * @param token The JWT token.
     * @return The username extracted from the token.
     * @throws ExpiredJwtException If the token has expired.
     * @throws JwtException If the token is invalid.
     */
    public String extractUsername(String token) {
        LOGGER.debug("Extracting username from JWT token");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String username = claims.getSubject();
            LOGGER.debug("Username extracted: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token expired while extracting username: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            LOGGER.error("Invalid JWT token while extracting username: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if a JWT token is valid for a specific user.
     *
     * @param token The JWT token to validate.
     * @param userDetails The user details to validate against.
     * @return True if the token is valid and matches the user, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        LOGGER.info("Checking if JWT token is valid for user: {}", userDetails.getUsername());
        try {
            String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            LOGGER.debug("Token validity for user {}: {}", userDetails.getUsername(), isValid);
            return isValid;
        } catch (JwtException e) {
            LOGGER.error("Error validating token for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a JWT token is expired.
     *
     * @param token The JWT token.
     * @return True if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith((SecretKey) SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            boolean isExpired = expiration.before(new Date());
            LOGGER.debug("Token expiration check: {}", isExpired);
            return isExpired;
        } catch (JwtException e) {
            LOGGER.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
}