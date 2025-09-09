package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDAO {
    private final UserRepository userRepository;

    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        return userRepository.save(user);
    }

    public Optional<User> findActiveByEmail(String email) {
        return userRepository.findByEmail(email).filter(u -> !u.isDeleted());
    }
}