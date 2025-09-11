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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing user-related operations such as registration, login, and profile updates.
 */
@Tag(name = "User Management", description = "Endpoints for user registration, authentication, and profile management")
@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Constructs a {@code UserController} with the specified service.
     *
     * @param userService The service that handles the business logic for user operations.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param userDTO The details for user registration.
     * @return A {@link ResponseEntity} containing a JWT token upon successful registration.
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account (guest or host) with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZXhwIjoxNzI1ODk4MDAwfQ.abc\""))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"The email is already in use\", \"code\": 400}")))
    })
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDTO> registerUser(
            @Parameter(description = "User registration details") @RequestBody @Schema(description = "User registration details") UserRegistrationDTO userDTO) {
        TokenResponseDTO token = userService.registerUser(userDTO);
        return ResponseEntity.status(201).body(token);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginDTO The user's login credentials.
     * @return A {@link ResponseEntity} containing the JWT token.
     */
    @Operation(summary = "Log in a user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZXhwIjoxNzI1ODk4MDAwfQ.abc\""))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Incorrect email or password\", \"code\": 401}")))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(
            @Parameter(description = "User login credentials") @RequestBody @Schema(description = "User login credentials") UserLoginDTO loginDTO) {
        TokenResponseDTO token = userService.loginUser(loginDTO);
        return ResponseEntity.ok(token);
    }

    /**
     * Updates the profile information of an authenticated user.
     *
     * @param userId    The ID of the user to update, provided in the request header.
     * @param updateDTO The profile update details.
     * @return A {@link ResponseEntity} containing the updated user profile.
     */
    @Operation(summary = "Update user profile", description = "Updates the profile information of an authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"Juan Perez\", \"phoneNumber\": \"+573001234567\", \"birthDate\": \"1990-05-15\", \"role\": \"GUEST\", \"profilePicture\": \"https://example.com/photo.jpg\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"The phone number is not in the correct format\", \"code\": 400}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"User not found\", \"code\": 404}")))
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Parameter(description = "User ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "Profile update details") @RequestBody @Schema(description = "Profile update details") UpdateProfileDTO updateDTO) {
        UserResponseDTO response = userService.updateProfile(userId, updateDTO);
        return ResponseEntity.ok(response);
    }
}