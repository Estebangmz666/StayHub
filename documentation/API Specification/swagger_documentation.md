# StayHub - Swagger Documentation

This document outlines the configuration and usage of Swagger for the StayHub application, utilizing `springdoc-openapi` to generate interactive API documentation. The Swagger UI is configured to be accessible at `/swagger-ui.html`, providing a user-friendly interface to explore and test the REST API endpoints.

## 1. Overview
- **Purpose**: Automatically generate and visualize API documentation based on Spring Boot controllers.
- **Tool**: `springdoc-openapi-starter-webmvc-ui`, compatible with Spring Boot 3+ and OpenAPI 3.
- **Access Point**: `http://localhost:8080/swagger-ui.html` (adjust port if different).

## 2. Configuration Steps

### 2.1 Add Dependencies
- Include the following dependency in your `pom.xml` (Maven) or `build.gradle` (Gradle) to enable Swagger UI.

- **Maven (`pom.xml`)**:
  ```xml
  <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
      <version>2.5.0</version> <!-- Use the latest version compatible with your Spring Boot version -->
  </dependency>
  ```
### 2.2 Configure Swagger-UI Path
- Set the Swagger UI path to /swagger-ui.html in your application.properties or application.yml.
- **application.properties**
    ```text
    springdoc.swagger-ui.path=/swagger-ui.html
    ```

### 2.3 Annotation Controllers
- Use **OpenAPI 3** annotations to document endpoints, improving the generated documentation.
- **Example Controller**
    ```java
    package edu.uniquindio.stayhub.api.controller;
    
    import edu.uniquindio.stayhub.api.dto.auth.UserRegistrationDTO;
    import edu.uniquindio.stayhub.api.service.UserService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    
    @RestController
    @RequestMapping("/api/users")
    public class UserController {
    
        @Autowired
        private UserService userService;
    
        @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details")
        @ApiResponse(responseCode = "201", description = "User registered successfully")
        @ApiResponse(responseCode = "400", description = "Invalid input data")
        @PostMapping("/register")
        public ResponseEntity<Void> registerUser(@RequestBody UserRegistrationDTO userDTO) {
            userService.registerUser(userDTO);
            return ResponseEntity.status(201).build();
        }
    }
    ```
- **Explanation:**
  - Permits access to Swagger UI and API docs endpoints without authentication.

## 3. Testing and Usage
- **Steps:**
  1. Run your springboot application
  ```bash
    cd stayhub-api
    ./mvnw spring-boot:run
  ```
  2. Open a Browser and navigate to `http://localhost:8080/swagger-ui.html`
  3. Explore the documented endpoints, test requests, and view response interactively.
- **Verification:** Ensure all annotated endpoints appear with correct details (e.g., parameters, responses).

## 4. Additional Notes
- **Version Compatibility:** Use the latest `springdoc-openapi` version compatible with your Spring Boot version (check Springdoc documentation).
- **Customization:** Add more annotations (e.g., @Parameter) or configure an openapi.yaml file for advanced customization.
- **Security:** Adjust the SecurityConfig if you use OAuth2 or other authentication methods.

## 5. Example Output
- After configuration, the Swagger UI will display a page like this (Example structure):
    - **Endpoint:** `POST /api/v1/users/register/`
    - **Description:** `Register a new user`
    - **Request Body:** `UserRegistrationDTO`
    - **Responses:** `201 (Succes), 400 (Bad Request)`