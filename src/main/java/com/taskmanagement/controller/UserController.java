package com.taskmanagement.controller;

import com.taskmanagement.dto.MessageResponse;
import com.taskmanagement.dto.UserDTO;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.UserMapper;
import com.taskmanagement.security.UserDetailsImpl;
import com.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management endpoints
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Get current user profile
     * GET /api/users/me
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("Fetching current user profile: {}", currentUser.getUsername());

        User user = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Fetching user by ID: {}", id);

        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    /**
     * Get all users with pagination
     * GET /api/users?page=0&size=10&sort=username
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sort) {

        log.info("Fetching all users - page: {}, size: {}, sort: {}", page, size, sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<UserDTO> users = userService.getAllUsers(pageable)
                .map(userMapper::toDTO);

        return ResponseEntity.ok(users);
    }

    /**
     * Update current user profile
     * PUT /api/users/me
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email) {

        log.info("Updating current user profile: {}", currentUser.getUsername());

        User updatedUser = userService.updateUser(currentUser.getId(), fullName, email);

        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

    /**
     * Change password
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        log.info("Changing password for user: {}", currentUser.getUsername());

        userService.changePassword(currentUser.getId(), oldPassword, newPassword);

        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    /**
     * Delete user (Admin only)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        log.info("Deleting user: {}", id);

        userService.deleteUser(id);

        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }
}
