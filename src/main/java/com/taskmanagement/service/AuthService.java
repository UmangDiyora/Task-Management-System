package com.taskmanagement.service;

import com.taskmanagement.entity.User;
import org.springframework.security.core.Authentication;

/**
 * Authentication Service Interface
 * Handles user registration, authentication, and JWT token generation
 */
public interface AuthService {

    /**
     * Register a new user
     * @param username Username
     * @param email Email address
     * @param password Raw password (will be encoded)
     * @param fullName Full name
     * @return Created user
     */
    User registerUser(String username, String email, String password, String fullName);

    /**
     * Authenticate user with username and password
     * @param username Username
     * @param password Password
     * @return Authentication object
     */
    Authentication authenticateUser(String username, String password);

    /**
     * Generate JWT token from authentication
     * @param authentication Authentication object
     * @return JWT token string
     */
    String generateJwtToken(Authentication authentication);

    /**
     * Validate if username already exists
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Validate if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
