package edu.uniquindio.stayhub.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Stayhub API application.
 * This class uses {@link SpringBootApplication} to enable autoconfiguration and
 * component scanning for the entire application. The security autoconfiguration
 * for the UserDetailsService is excluded because a custom one is provided.
 */
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class StayhubApiApplication {
    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(StayhubApiApplication.class, args);
    }
}