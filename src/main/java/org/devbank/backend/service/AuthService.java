package org.devbank.backend.service;

import org.devbank.backend.entity.User;
import org.devbank.backend.exception.UnauthorizedException;
import org.devbank.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!user.getPassword().equals(password)) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException("User account is inactive");
        }

        return user;
    }
}