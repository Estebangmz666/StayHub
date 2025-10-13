package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.auth.TokenResponseDTO;
import edu.uniquindio.stayhub.api.dto.auth.passwordReset.PasswordResetRequestDTO;
import edu.uniquindio.stayhub.api.dto.auth.passwordReset.ResetPasswordDTO;
import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserLoginDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.exception.EmailAlreadyExistsException;
import edu.uniquindio.stayhub.api.exception.InvalidPasswordException;
import edu.uniquindio.stayhub.api.exception.InvalidTokenException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.mapper.UserMapper;
import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.PasswordResetTokenRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private JwtService jwtService;
    @Mock private EmailService emailService;

    @InjectMocks private UserService userService;

    private User user;
    private final Long userId = 1L;
    private final String email = "test@user.com";
    private final String rawPassword = "password123";
    private final String encodedPassword = "$2a$10$encodedhash";

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(Role.GUEST);
    }

    // ----------------------------------------------------------------------
    // Tests para loadUserByUsername
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should load UserDetails successfully for existing user")
    void loadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);
        assertThat(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"))).isTrue();
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException for non-existent user")
    void loadUserByUsername_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado con email");
    }

    // ----------------------------------------------------------------------
    // Tests para registerUser
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should register new user successfully")
    void registerUser_Success() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("Test", email, rawPassword, Role.GUEST);
        User newUser = new User();
        User savedUser = user;
        UserResponseDTO responseDTO = new UserResponseDTO(userId, "Test", email, Role.GUEST, null);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userMapper.toEntity(registrationDTO)).thenReturn(newUser);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(newUser)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(responseDTO);

        // Mock the userMapper methods that are called on newUser
        doNothing().when(userMapper).setHostProfile(newUser);

        // Act
        UserResponseDTO result = userService.registerUser(registrationDTO);

        // Assert
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userMapper, times(1)).setHostProfile(newUser);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException if email is already in use")
    void registerUser_EmailExists_ThrowsException() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO("Test", email, rawPassword, Role.GUEST);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(registrationDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("El correo electrónico ya está en uso");
        verify(userRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para loginUser
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should login user successfully and return TokenResponseDTO")
    void loginUser_Success() {
        // Arrange
        UserLoginDTO loginDTO = new UserLoginDTO(email, rawPassword);
        String token = "mocked.jwt.token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        // Act
        TokenResponseDTO result = userService.loginUser(loginDTO);

        // Assert
        assertThat(result.getToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException if user email is not found during login")
    void loginUser_UserNotFound_ThrowsException() {
        // Arrange
        UserLoginDTO loginDTO = new UserLoginDTO(email, rawPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loginUser(loginDTO))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
        verify(jwtService, never()).generateToken((User) any());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException if password does not match")
    void loginUser_InvalidPassword_ThrowsException() {
        // Arrange
        UserLoginDTO loginDTO = new UserLoginDTO(email, rawPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.loginUser(loginDTO))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Correo electrónico o contraseña incorrectos");
        verify(jwtService, never()).generateToken((User) any());
    }

    // ----------------------------------------------------------------------
    // Tests para updateProfile
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should update user profile successfully")
    void updateProfile_Success() {
        // Arrange
        UpdateProfileDTO updateDTO = new UpdateProfileDTO("New Name", "New City", "1234567890", null, null);
        UserResponseDTO responseDTO = new UserResponseDTO(userId, "New Name", email, Role.GUEST, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(updateDTO, user);
        doNothing().when(userMapper).updateHostProfile(updateDTO, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.updateProfile(userId, updateDTO);

        // Assert
        assertThat(result.getName()).isEqualTo("New Name");
        verify(userMapper, times(1)).updateUser(updateDTO, user);
        verify(userMapper, times(1)).updateHostProfile(updateDTO, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when updating profile of non-existent user")
    void updateProfile_UserNotFound_ThrowsException() {
        // Arrange
        UpdateProfileDTO updateDTO = new UpdateProfileDTO("New Name", "New City", "1234567890", null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.updateProfile(userId, updateDTO))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para requestPasswordReset
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should successfully request password reset, generate token, and send email")
    void requestPasswordReset_Success(){
        // Arrange
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(email);
        LocalDateTime fixedTime = LocalDateTime.of(2025, 10, 25, 10, 30);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.countByUserAndUsedFalse(user)).thenReturn(0L);
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).sendPasswordResetEmail(eq(email), anyString());

        // Usamos Mockito.mockStatic para interceptar LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(fixedTime);

            // Act
            userService.requestPasswordReset(requestDTO);

            // Assert
            verify(passwordResetTokenRepository, times(1)).deleteByUser(user);
            verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
            verify(emailService, times(1)).sendPasswordResetEmail(eq(email), anyString());
        }
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when requesting reset for non-existent user")
    void requestPasswordReset_UserNotFound_ThrowsException() {
        // Arrange
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.requestPasswordReset(requestDTO))
                .isInstanceOf(UserNotFoundException.class);
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException if too many active tokens exist")
    void requestPasswordReset_TooManyTokens_ThrowsException() {
        // Arrange
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.countByUserAndUsedFalse(user)).thenReturn(3L);

        // Act & Assert
        assertThatThrownBy(() -> userService.requestPasswordReset(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Demasiadas solicitudes de restablecimiento");
        verify(passwordResetTokenRepository, never()).deleteByUser(any());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    // ----------------------------------------------------------------------
    // Tests para resetPassword
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should reset password successfully with a valid token")
    void resetPassword_Success() {
        // Arrange
        String token = "valid_token";
        String newPassword = "newpassword123";
        ResetPasswordDTO resetDTO = new ResetPasswordDTO(token, newPassword);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(5)); // No expirado
        resetToken.setUsed(false);

        when(passwordResetTokenRepository.findByTokenAndUsedFalse(token)).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(newPassword)).thenReturn("newencodedhash");

        // Act
        userService.resetPassword(resetDTO);

        // Assert
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).save(resetToken);
        assertThat(resetToken.isUsed()).isTrue();
    }

    @Test
    @DisplayName("Should throw InvalidTokenException if token is invalid or already used")
    void resetPassword_InvalidToken_ThrowsException() {
        // Arrange
        String token = "invalid_token";
        ResetPasswordDTO resetDTO = new ResetPasswordDTO(token, "newpassword");
        when(passwordResetTokenRepository.findByTokenAndUsedFalse(token)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.resetPassword(resetDTO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token inválido o ya utilizado");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException if token has expired")
    void resetPassword_ExpiredToken_ThrowsException() {
        // Arrange
        String token = "expired_token";
        ResetPasswordDTO resetDTO = new ResetPasswordDTO(token, "newpassword");

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setExpiryDate(LocalDateTime.now().minusMinutes(5)); // Expirado
        resetToken.setUsed(false);

        when(passwordResetTokenRepository.findByTokenAndUsedFalse(token)).thenReturn(Optional.of(resetToken));

        // Act & Assert
        assertThatThrownBy(() -> userService.resetPassword(resetDTO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("El token ha expirado");
        verify(userRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para getProfile
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should return user profile successfully")
    void getProfile_Success() {
        // Arrange
        UserResponseDTO responseDTO = new UserResponseDTO(userId, "Test", email, Role.GUEST, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.getProfile(userId);

        // Assert
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponseDto(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when fetching profile of non-existent user")
    void getProfile_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getProfile(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}