package edu.uniquindio.stayhub.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI 3.0 documentation for the StayHub API.
 * Defines metadata such as title, version, description, contact information, and license,
 * as well as server configurations and security schemes for JWT authentication.
 * This class is used to generate Swagger UI documentation for the API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "StayHub API",
                version = "1.0.0",
                description = "REST API for managing accommodations, reservations, and user accounts in the StayHub platform",
                contact = @Contact(
                        name = "Esteban Gómez León",
                        email = "estebangumy05@gmail.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Development Server"
                ),
                @Server(
                        url = "https://api-test.stayhub.com",
                        description = "Test Server"
                )
        }
)
public class OpenApiConfig {
    /**
     * Configures and provides an OpenAPI bean for Swagger UI documentation.
     * Includes a security scheme for Bearer Authentication using JWT tokens,
     * enabling secure access to protected endpoints in the StayHub API.
     *
     * @return An OpenAPI instance with custom configurations for StayHub.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token for authentication")
                        )
                );
    }
}