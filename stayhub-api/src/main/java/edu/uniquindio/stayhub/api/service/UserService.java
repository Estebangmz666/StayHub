package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.exception.InvalidPasswordException;
import edu.uniquindio.stayhub.api.exception.InvalidTokenException;
import edu.uniquindio.stayhub.api.exception.UserNotFoundException;
import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import edu.uniquindio.stayhub.api.model.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(@Valid User user) {
        if (user.getRole() == Role.HOST && user.getHostProfile() == null) {
            user.setHostProfile(new HostProfile());
        } else if (user.getRole() == Role.USER) {
            user.setHostProfile(null);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateProfile(Long userId, @Valid UpdateProfileDTO updateUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setProfilePicture(updatedUser.getProfilePicture());
        if (user.getRole() == Role.HOST) {
            HostProfile profile = user.getHostProfile();
            if (profile == null) {
                profile = new HostProfile();
                user.setHostProfile(profile);
            }
            profile.setDescription(updatedUser.getDescription());
            profile.setLegalDocuments(updatedUser.getLegalDocuments());
        }
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }
    }

    public void createPasswordResetToken(User user, String token) {
        passwordResetTokenRepository.deleteByUser(user);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetTokenRepository.save(resetToken);
    }

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
}