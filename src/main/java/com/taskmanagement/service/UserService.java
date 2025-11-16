package com.taskmanagement.service;

import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * User Service Interface
 * Handles CRUD operations for users
 */
public interface UserService {

    /**
     * Get user by ID
     * @param id User ID
     * @return User if found
     */
    Optional<User> getUserById(Long id);

    /**
     * Get user by username
     * @param username Username
     * @return User if found
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Get user by email
     * @param email Email
     * @return User if found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Get all users with pagination
     * @param pageable Pagination parameters
     * @return Page of users
     */
    Page<User> getAllUsers(Pageable pageable);

    /**
     * Update user
     * @param userId User ID
     * @param fullName New full name (optional)
     * @param email New email (optional)
     * @return Updated user
     */
    User updateUser(Long userId, String fullName, String email);

    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Old password for verification
     * @param newPassword New password
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Delete user
     * @param userId User ID
     */
    void deleteUser(Long userId);

    /**
     * Check if user exists by ID
     * @param userId User ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long userId);
}
