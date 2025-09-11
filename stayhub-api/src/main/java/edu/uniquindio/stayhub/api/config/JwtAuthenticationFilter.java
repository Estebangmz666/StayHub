package edu.uniquindio.stayhub.api.config;

import edu.uniquindio.stayhub.api.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for validating JWT tokens in incoming HTTP requests for the StayHub application.
 * This filter extends {@link OncePerRequestFilter} to ensure it's executed only once per request.
 * It is responsible for extracting the JWT token from the Authorization header, validating it
 * using {@link JwtService}, and setting the authentication in the {@link SecurityContextHolder}
 * if the token is valid. It also handles common JWT exceptions like token expiration.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs a JwtAuthenticationFilter with the required dependencies.
     *
     * @param jwtService The service for handling JWT token validation and creation.
     * @param userDetailsService The service for loading user details based on a username.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Processes each HTTP request to extract and validate the JWT token from the Authorization header.
     * <p>
     * The process is as follows:
     * <ol>
     * <li>Checks for the presence of the "Authorization" header and if it starts with "Bearer ".</li>
     * <li>Extracts the token and attempts to get the username from it.</li>
     * <li>If the username is valid and no authentication has been set in the security context, it loads the user details.</li>
     * <li>Validates the token against the user details.</li>
     * <li>If the token is valid, it creates a new {@link UsernamePasswordAuthenticationToken} and sets it in the security context.</li>
     * <li>Handles {@link ExpiredJwtException} and other {@link JwtException}s by logging the error and setting the HTTP status to 401 Unauthorized.</li>
     * </ol>
     *
     * @param request The HTTP request containing the JWT token in the Authorization header.
     * @param response The HTTP response.
     * @param filterChain The filter chain to continue processing the request.
     * @throws ServletException If a servlet error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        LOGGER.info("Successfully authenticated user: {} for request: {}", username, request.getRequestURI());
                    } else {
                        LOGGER.warn("Invalid JWT token for user: {} for request: {}", username, request.getRequestURI());
                    }
                }
            } catch (ExpiredJwtException e) {
                LOGGER.error("JWT token expired for request: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            } catch (JwtException e) {
                LOGGER.error("Invalid JWT token for request: {}", request.getRequestURI(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        } else {
            LOGGER.debug("No JWT token found in request: {}", request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}