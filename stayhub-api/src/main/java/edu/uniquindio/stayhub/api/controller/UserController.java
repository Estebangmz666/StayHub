package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.auth.Error;
import edu.uniquindio.stayhub.api.dto.auth.TokenResponseDTO;
import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserLoginDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user-related operations such as registration, login, and profile updates.
 */
@Tag(name = "User Management", description = "Endpoints for user registration, authentication, and profile management")
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account (guest or host) with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZXhwIjoxNzI1ODk4MDAwfQ.abc\""))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El correo electrónico ya está en uso\", \"code\": 400}")))
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> registerUser(
            @RequestBody @Schema(description = "User registration details") UserRegistrationDTO userDTO) {
        TokenResponseDTO token = userService.registerUser(userDTO);
        return ResponseEntity.status(201).body(token);
    }

    @Operation(summary = "Log in a user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZXhwIjoxNzI1ODk4MDAwfQ.abc\""))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Correo electrónico o contraseña incorrectos\", \"code\": 401}")))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(
            @RequestBody @Schema(description = "User login credentials") UserLoginDTO loginDTO) {
        TokenResponseDTO token = userService.loginUser(loginDTO);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Update user profile", description = "Updates the profile information of an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"Juan Perez\", \"phoneNumber\": \"+573001234567\", \"birthDate\": \"1990-05-15\", \"role\": \"GUEST\", \"profilePicture\": \"https://example.com/photo.jpg\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"El número de teléfono no tiene el formato correcto\", \"code\": 400}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Usuario no encontrado\", \"code\": 404}")))
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Schema(description = "Profile update details") UpdateProfileDTO updateDTO) {
        UserResponseDTO response = userService.updateProfile(userId, updateDTO);
        return ResponseEntity.ok(response);
    }
}