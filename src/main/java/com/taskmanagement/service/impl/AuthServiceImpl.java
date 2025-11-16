package com.taskmanagement.service.impl;

import com.taskmanagement.entity.Role;
import com.taskmanagement.entity.RoleName;
import com.taskmanagement.entity.User;
import com.taskmanagement.repository.RoleRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.security.JwtUtils;
import com.taskmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Authentication Service Implementation
 * Handles user registration, authentication, and JWT token generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Register a new user with default ROLE_USER
     */
    @Override
    @Transactional
    public User registerUser(String username, String email, String password, String fullName) {
        log.info("Registering new user: {}", username);

        // Validate username and email uniqueness
        if (userRepository.existsByUsername(username)) {
            log.error("Username already exists: {}", username);
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(email)) {
            log.error("Email already exists: {}", email);
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName);

        // Assign default role (ROLE_USER)
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("Default role ROLE_USER not found in database");
                    return new RuntimeException("Error: Role ROLE_USER not found");
                });
        roles.add(userRole);
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} with ID: {}", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }

    /**
     * Authenticate user with username and password
     */
    @Override
    public Authentication authenticateUser(String username, String password) {
        log.info("Authenticating user: {}", username);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            log.info("User authenticated successfully: {}", username);
            return authentication;

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", username, e);
            throw new RuntimeException("Invalid username or password");
        }
    }

    /**
     * Generate JWT token from authentication
     */
    @Override
    public String generateJwtToken(Authentication authentication) {
        String token = jwtUtils.generateJwtToken(authentication);
        log.debug("JWT token generated for user: {}", authentication.getName());
        return token;
    }

    /**
     * Check if username exists
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
