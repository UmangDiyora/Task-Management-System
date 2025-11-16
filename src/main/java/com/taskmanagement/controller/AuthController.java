package com.taskmanagement.controller;

import com.taskmanagement.dto.JwtResponse;
import com.taskmanagement.dto.LoginRequest;
import com.taskmanagement.dto.MessageResponse;
import com.taskmanagement.dto.SignupRequest;
import com.taskmanagement.entity.User;
import com.taskmanagement.security.UserDetailsImpl;
import com.taskmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Registration request for username: {}", signupRequest.getUsername());

        // Check if username already exists
        if (authService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email already exists
        if (authService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user
        User user = authService.registerUser(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                signupRequest.getFullName()
        );

        log.info("User registered successfully: {}", user.getUsername());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

    /**
     * Authenticate user and return JWT token
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for username: {}", loginRequest.getUsername());

        // Authenticate user
        Authentication authentication = authService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        // Generate JWT token
        String jwt = authService.generateJwtToken(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("User authenticated successfully: {}", userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                roles
        ));
    }

    /**
     * Test endpoint to verify authentication
     * GET /api/auth/test
     */
    @GetMapping("/test")
    public ResponseEntity<MessageResponse> testAuth() {
        return ResponseEntity.ok(new MessageResponse("Authentication endpoints are working!"));
    }
}
