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
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.PasswordResetTokenRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Validated
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    @Transactional
    public UserResponseDTO registerUser(@Valid UserRegistrationDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El correo electrónico ya está en uso");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userMapper.setHostProfile(user);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public TokenResponseDTO loginUser(@Valid UserLoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Correo electrónico o contraseña incorrectos");
        }
        String token = jwtService.generateToken(user);
        return new TokenResponseDTO(token);
    }

    @Transactional
    public UserResponseDTO updateProfile(Long userId, @Valid UpdateProfileDTO updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        userMapper.updateUser(updatedUser, user);
        userMapper.updateHostProfile(updatedUser, user);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

//    @Transactional
//    public void changePassword(Long userId, String currentPassword, String newPassword) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
//        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
//            throw new InvalidPasswordException("La contraseña actual es incorrecta");
//        }
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }

    @Transactional
    public void requestPasswordReset(@Valid PasswordResetRequestDTO requestDTO) {
        LOGGER.info("Processing password reset request for email: {}", requestDTO.getEmail());
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElseThrow(()
                -> new UserNotFoundException("Usuario no encontrado"));

        String firstName = user.getName().split(" ")[0];

        Long activeTokens = passwordResetTokenRepository.countByUserAndUsedFalse(user);
        if (activeTokens >= 3) {
            LOGGER.warn("Too many active password reset tokens for user: {}", user.getEmail());
            throw new IllegalStateException("Demasiadas solicitudes de restablecimiento de contraseña. Intente de nuevo más tarde.");
        }

        passwordResetTokenRepository.deleteByUser(user);
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        resetToken.setUsed(false);
        passwordResetTokenRepository.save(resetToken);
        LOGGER.debug("Password reset token generated for user: {}", user.getEmail());
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token, firstName);
            LOGGER.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            LOGGER.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Error al enviar el correo de restablecimiento");
        }
    }

    @Transactional
    public void resetPassword(@Valid ResetPasswordDTO resetDTO) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(resetDTO.getToken())
                .orElseThrow(() -> new InvalidTokenException("Token inválido o ya utilizado"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El token ha expirado");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
        userRepository.save(user);
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getProfile(Long userId) {
        LOGGER.info("Fetching profile for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        UserResponseDTO response = userMapper.toResponseDto(user);
        LOGGER.debug("Profile retrieved successfully for user ID: {}", userId);
        return response;
    }

}