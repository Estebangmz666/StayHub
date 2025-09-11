package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.auth.TokenResponseDTO;
import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserLoginDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.exception.InvalidPasswordException;
import edu.uniquindio.stayhub.api.exception.InvalidTokenException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.mapper.UserMapper;
import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.Role;
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
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * Service class for managing user-related operations, including authentication, registration,
 * profile updates, and password management.
 */
@Validated
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    /**
     * Loads a user by their username (email) for Spring Security authentication.
     *
     * @param email The email of the user to load.
     * @return A UserDetails object for the authenticated user.
     * @throws UsernameNotFoundException if the user with the given email is not found.
     */
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

    /**
     * Saves a new user to the database after encoding their password.
     *
     * @param user The user entity to be saved.
     * @return The saved user entity.
     */
    public User saveUser(@Valid User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Updates an existing user's profile information. This includes general user details
     * and specific details for hosts if the user's role is HOST.
     *
     * @param userId The ID of the user to update.
     * @param updatedUser The DTO containing the new profile information.
     * @return A DTO representing the updated user's profile.
     * @throws UserNotFoundException if the user with the given ID does not exist.
     */
    public UserResponseDTO updateProfile(Long userId, @Valid UpdateProfileDTO updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setProfilePicture(updatedUser.getProfilePicture());
        if (user.getRole() == Role.HOST) {
            HostProfile profile = user.getHostProfile();
            if (profile == null) {
                profile = new HostProfile();
                profile.setUser(user);
                user.setHostProfile(profile);
            }
            if (updatedUser.getDescription() != null) {
                profile.setDescription(updatedUser.getDescription());
            }
            if (updatedUser.getLegalDocuments() != null) {
                profile.setLegalDocuments(updatedUser.getLegalDocuments());
            }
        }
        User updatedUserEntity = userRepository.save(user);
        return userMapper.toResponseDto(updatedUserEntity);
    }

    /**
     * Changes a user's password after validating their current password.
     *
     * @param userId The ID of the user whose password is to be changed.
     * @param currentPassword The user's current password.
     * @param newPassword The new password to be set.
     * @throws UserNotFoundException if the user with the given ID does not exist.
     * @throws InvalidPasswordException if the provided current password does not match the stored password.
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }
    }

    /**
     * Creates and saves a new password reset token for a user.
     * It first deletes any existing token for the user.
     *
     * @param user The user for whom the token is being created.
     * @param token The token string.
     */
    public void createPasswordResetToken(User user, String token) {
        passwordResetTokenRepository.deleteByUser(user);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);
    }

    /**
     * Resets a user's password using a valid password reset token.
     *
     * @param token The password reset token.
     * @param newPassword The new password to be set.
     * @throws InvalidTokenException if the token is invalid or has expired.
     */
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El token ha expirado");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    /**
     * Registers a new user with the system. It checks for an existing email,
     * encrypts the password, creates the user entity, and returns a JWT token.
     *
     * @param userDTO The DTO containing the user registration details.
     * @return A DTO containing the generated JWT token.
     * @throws IllegalStateException if the email provided is already in use.
     */
    public TokenResponseDTO registerUser(@Valid UserRegistrationDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está en uso");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userMapper.setHostProfile(user);
        user = saveUser(user);
        String token = jwtService.generateToken(user);
        return new TokenResponseDTO(token);
    }

    /**
     * Authenticates a user based on their login credentials. If successful,
     * it returns a JWT token.
     *
     * @param loginDTO The DTO containing the user's email and password.
     * @return A DTO containing the generated JWT token.
     * @throws UserNotFoundException if the user's email is not found.
     * @throws InvalidPasswordException if the password does not match the stored password.
     */
    public TokenResponseDTO loginUser(@Valid UserLoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Correo electrónico o contraseña incorrectos");
        }
        String token = jwtService.generateToken(user);
        return new TokenResponseDTO(token);
    }
}