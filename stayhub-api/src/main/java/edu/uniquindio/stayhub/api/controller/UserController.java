package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.config.RateLimiter;
import edu.uniquindio.stayhub.api.dto.auth.TokenResponseDTO;
import edu.uniquindio.stayhub.api.dto.auth.passwordReset.PasswordResetRequestDTO;
import edu.uniquindio.stayhub.api.dto.auth.passwordReset.ResetPasswordDTO;
import edu.uniquindio.stayhub.api.dto.responses.SuccessResponseDTO;
import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserLoginDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.service.UserService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "Endpoints for user registration, authentication, and profile management")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Register a new user", description = "Creates a new user account (guest or host) with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"john.doe@example.com\", \"name\": \"John Doe\", \"phoneNumber\": \"+573101234567\", \"birthDate\": \"1995-08-20\", \"role\": \"GUEST\", \"profilePicture\": null, \"description\": null, \"legalDocuments\": null}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid email format\", \"code\": 400}"))),
            @ApiResponse(responseCode = "409", description = "Email already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Email already exists\", \"code\": 409}")))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody @Parameter(description = "User registration details") UserRegistrationDTO userDTO) {
        LOGGER.info("Processing user registration for email: {}", userDTO.getEmail());
        UserResponseDTO response = userService.registerUser(userDTO);
        LOGGER.debug("User registered successfully: {}", userDTO.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Log in a user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzUxMiJ9...\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid email format\", \"code\": 400}"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Incorrect email or password\", \"code\": 401}")))
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> loginUser(
            @Valid @RequestBody @Parameter(description = "User login credentials") UserLoginDTO loginDTO) {
        LOGGER.info("Processing login for email: {}", loginDTO.getEmail());
        TokenResponseDTO token = userService.loginUser(loginDTO);
        LOGGER.debug("User logged in successfully: {}", loginDTO.getEmail());
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Operation(summary = "Update user profile", description = "Updates the profile information of an authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"Juan Perez\", \"phoneNumber\": \"+573001234567\", \"birthDate\": \"1990-05-15\", \"role\": \"GUEST\", \"profilePicture\": \"https://example.com/photo.jpg\", \"description\": null, \"legalDocuments\": null}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"The phone number is not in the correct format\", \"code\": 400}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"User not found\", \"code\": 404}")))
    })
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestHeader("X-User-Id") @Parameter(description = "User ID", required = true) Long userId,
            @Valid @RequestBody @Parameter(description = "Profile update details") UpdateProfileDTO updateDTO) {
        LOGGER.info("Processing profile update for user ID: {}", userId);
        UserResponseDTO response = userService.updateProfile(userId, updateDTO);
        LOGGER.debug("Profile updated successfully for user ID: {}", userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Request password reset", description = "Initiates a password reset process by sending a reset token to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset request processed successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid email format\", \"code\": 400}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"User not found\", \"code\": 404}"))),
            @ApiResponse(responseCode = "429", description = "Too many reset requests",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Too many reset requests\", \"code\": 429}")))
    })
    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(
            @Valid @RequestBody @Parameter(description = "User email") PasswordResetRequestDTO requestDTO) {
        LOGGER.info("Processing password reset request for email: {}", requestDTO.getEmail());
        Bucket bucket = rateLimiter.resolveBucket(requestDTO.getEmail());
        if (!bucket.tryConsume(1)) {
            LOGGER.warn("Rate limit exceeded for email: {}", requestDTO.getEmail());
            throw new IllegalStateException("Demasiadas solicitudes de restablecimiento de contraseña");
        }
        userService.requestPasswordReset(requestDTO);
        LOGGER.debug("Password reset request processed successfully for email: {}", requestDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Reset password", description = "Resets the password using a valid token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Invalid token\", \"code\": 400}"))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Token has expired\", \"code\": 401}")))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<SuccessResponseDTO> resetPassword(
            @Valid @RequestBody @Parameter(description = "Token and new password") ResetPasswordDTO resetDTO) {
        LOGGER.info("Processing password reset request");
        userService.resetPassword(resetDTO);
        LOGGER.debug("Password reset successfully");
        return new ResponseEntity<>(new SuccessResponseDTO("Password reset successfully"), HttpStatus.OK);
    }

    @Operation(summary = "Get user profile", description = "Retrieves the profile information of the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"email\": \"user@example.com\", \"name\": \"Juan Perez\", \"phoneNumber\": \"+573001234567\", \"birthDate\": \"1990-05-15\", \"role\": \"GUEST\", \"profilePicture\": \"https://example.com/photo.jpg\", \"description\": null, \"legalDocuments\": null}"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = edu.uniquindio.stayhub.api.dto.auth.Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"User not found\", \"code\": 404}")))
    })
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(
            @RequestHeader("X-User-Id") @Parameter(description = "User ID", required = true) Long userId) {
        LOGGER.info("Fetching profile for user ID: {}", userId);
        UserResponseDTO response = userService.getProfile(userId);
        LOGGER.debug("Profile fetched successfully for user ID: {}", userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}