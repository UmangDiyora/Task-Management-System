package com.taskmanagement.service.impl;

import com.taskmanagement.entity.User;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Service Implementation
 * Handles CRUD operations for users
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get user by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable);
    }

    /**
     * Update user information
     */
    @Override
    @Transactional
    public User updateUser(Long userId, String fullName, String email) {
        log.info("Updating user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        // Update full name if provided
        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }

        // Update email if provided and not already taken
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                log.error("Email already exists: {}", email);
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(email);
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());

        return updatedUser;
    }

    /**
     * Change user password
     */
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with ID: " + userId);
                });

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.error("Old password is incorrect for user: {}", userId);
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userId);
    }

    /**
     * Delete user
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new RuntimeException("User not found with ID: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
    }

    /**
     * Check if user exists by ID
     */
    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
