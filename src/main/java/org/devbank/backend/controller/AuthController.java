package org.devbank.backend.controller;

import org.devbank.backend.dto.LoginRequest;
import org.devbank.backend.dto.LoginResponse;
import org.devbank.backend.entity.User;
import org.devbank.backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        User user = authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        LoginResponse response = new LoginResponse(
                "Login successful",
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                "Logout successful"
        );
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> currentUser(
            HttpSession session) {

        Object userId =
                session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        LoginResponse response =
                new LoginResponse(
                        "Authenticated",
                        (Long) session.getAttribute("userId"),
                        (String) session.getAttribute("username"),
                        (String) session.getAttribute("role")
                );

        return ResponseEntity.ok(response);
    }
}