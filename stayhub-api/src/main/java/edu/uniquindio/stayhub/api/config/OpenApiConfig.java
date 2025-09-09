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
 * Configuration class for OpenAPI (Swagger) documentation of the StayHub API.
 * This class defines the API metadata, servers, and security schemes using Springdoc OpenAPI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "StayHub API",
                version = "1.0.0",
                description = "REST API for managing accommodations, reservations, and user accounts in the StayHub platform",
                contact = @Contact(
                        name = "StayHub Development Team",
                        email = "estebangumy05@gmail.com",
                        url = "https://stayhub.com"
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
     * Creates and configures the OpenAPI bean for Swagger UI.
     * Includes a security scheme for Bearer Authentication (JWT) if implemented later.
     *
     * @return OpenAPI instance with custom configurations
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