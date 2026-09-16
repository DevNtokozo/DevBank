package org.devbank.backend.service;

import org.devbank.backend.entity.User;
import org.devbank.backend.exception.UnauthorizedException;
import org.devbank.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldAuthenticateValidUser() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        User result =
                authService.authenticate(
                        "testuser",
                        "Test@123"
                );

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    void shouldRejectInvalidPassword() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        assertThrows(
                UnauthorizedException.class,
                () -> authService.authenticate(
                        "testuser",
                        "WrongPassword"
                )
        );
    }

    @Test
    void shouldRejectUnknownUser() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedException.class,
                () -> authService.authenticate(
                        "unknown",
                        "Test@123"
                )
        );
    }

    @Test
    void shouldRejectInactiveUser() {

        User user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "INACTIVE"
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UnauthorizedException exception =
                assertThrows(
                        UnauthorizedException.class,
                        () -> authService.authenticate(
                                "testuser",
                                "Test@123"
                        )
                );

        assertEquals(
                "User account is inactive",
                exception.getMessage()
        );
    }
}