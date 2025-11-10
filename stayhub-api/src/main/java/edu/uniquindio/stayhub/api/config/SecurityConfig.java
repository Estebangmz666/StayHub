package edu.uniquindio.stayhub.api.config;

import edu.uniquindio.stayhub.api.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main security configuration class for the StayHub application.
 * This class configures the security filter chain, password encoder, and CORS settings.
 * It integrates a custom JWT authentication filter to secure API endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructs a {@code SecurityConfig} with the required services.
     * The {@code UserDetailsService} is lazily loaded to avoid circular dependencies.
     *
     * @param jwtService The service for handling JWT token operations.
     * @param userDetailsService The service for loading user-specific data.
     */
    public SecurityConfig(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return An instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * This method:
     * <ul>
     * <li>Disables CSRF protection as the application is stateless and uses JWTs.</li>
     * <li>Configures CORS using the {@link #corsConfigurationSource()} bean.</li>
     * <li>Authorizes requests, allowing access to Swagger UI, login, and registration endpoints without authentication.</li>
     * <li>Requires authentication for all other endpoints.</li>
     * <li>Adds the {@link JwtAuthenticationFilter} before the default Spring Security filter.</li>
     * </ul>
     *
     * @param http The {@link HttpSecurity} object to configure.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // === PUBLIC ENDPOINTS ===
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll() // TODO: Replace in prod

                                // ==== USER ENDPOINTS (public login/register/password reset) ====
                                .requestMatchers(
                                        "/api/v1/users/register",
                                        "/api/v1/users/login",
                                        "/api/v1/users/request-password-reset",
                                        "/api/v1/users/reset-password"
                                ).permitAll()

                                // ==== ACCOMMODATION PUBLIC ENDPOINTS ====
                                .requestMatchers(
                                        "/api/v1/accommodations",
                                        "/api/v1/accommodations/search",
                                        "/api/v1/accommodations/{id}"
                                ).permitAll()

                                // ==== AMENITY PUBLIC ENDPOINTS ====
                                .requestMatchers(
                                        "/api/v1/amenities",
                                        "/api/v1/amenities/**"
                                ).permitAll()

                                // ==== ACCOMMODATION PROTECTED ENDPOINTS ====
                                .requestMatchers(
                                        "POST", "/api/v1/accommodations"
                                ).authenticated()
                                .requestMatchers(
                                        "PUT", "/api/v1/accommodations/**"
                                ).authenticated()
                                .requestMatchers(
                                        "DELETE", "/api/v1/accommodations/**"
                                ).authenticated()

                                // ==== USER PROTECTED ENDPOINTS ====
                                .requestMatchers(
                                        "PUT", "/api/v1/users/profile"
                                ).authenticated()

                                // ==== RESERVATION PROTECTED ENDPOINTS ====
                                .requestMatchers(
                                        "GET", "/api/v1/reservations/**",
                                        "POST", "/api/v1/reservations/**",
                                        "PUT", "/api/v1/reservations/**",
                                        "DELETE", "/api/v1/reservations/**"
                                ).authenticated()

                                // ==== COMMENT PUBLIC ENDPOINTS ====
                                .requestMatchers(
                                        "/api/v1/comments/accommodation/{accommodationId}",
                                        "/api/v1/comments/accommodation/{accommodationId}/paged",
                                        "/api/v1/comments/accommodation/{accommodationId}/average-rating",
                                        "/api/v1/comments/accommodation/{accommodationId}/count"
                                ).permitAll()

                                // ==== COMMENT PROTECTED ENDPOINTS ====
                                .requestMatchers(
                                        "POST", "/api/v1/comments"
                                ).authenticated()
                                .requestMatchers(
                                        "GET", "/api/v1/comments/user/{userId}"
                                ).authenticated()
                                .requestMatchers(
                                        "PUT", "/api/v1/comments/{id}"
                                ).authenticated()
                                .requestMatchers(
                                        "DELETE", "/api/v1/comments/{id}"
                                ).authenticated()
                                .requestMatchers(
                                        "POST", "/api/v1/comments/{id}/reply"
                                ).authenticated()


                                // ==== DEFAULT RULE ====
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures the CORS policy for the application.
     * This bean allows cross-origin requests from `<a href="http://localhost:3030">LocalHost:3030</a>` with specified methods, headers, and credentials.
     *
     * @return An instance of {@link CorsConfigurationSource}.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Provides a {@link JwtAuthenticationFilter} bean.
     *
     * @return An instance of {@link JwtAuthenticationFilter}.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}