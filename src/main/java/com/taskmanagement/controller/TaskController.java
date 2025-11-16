package com.taskmanagement.controller;

import com.taskmanagement.dto.CreateTaskRequest;
import com.taskmanagement.dto.MessageResponse;
import com.taskmanagement.dto.TaskDTO;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.TaskMapper;
import com.taskmanagement.security.UserDetailsImpl;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for task management endpoints
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskMapper taskMapper;

    /**
     * Create a new task
     * POST /api/tasks
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Creating new task: {} by user: {}", request.getTitle(), currentUser.getUsername());

        User creator = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskService.createTask(
                request.getTitle(),
                request.getDescription(),
                request.getProjectId(),
                creator,
                request.getPriority(),
                request.getDueDate()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskMapper.toDTO(task));
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        log.info("Fetching task by ID: {}", id);

        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    /**
     * Get all tasks with pagination
     * GET /api/tasks?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all tasks - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDTO> tasks = taskService.getAllTasks(pageable)
                .map(taskMapper::toDTO);

        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by project
     * GET /api/tasks/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDTO>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching tasks for project: {}", projectId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDTO> tasks = taskService.getTasksByProject(projectId, pageable)
                .map(taskMapper::toDTO);

        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks assigned to current user
     * GET /api/tasks/my
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDTO>> getMyTasks(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching tasks assigned to user: {}", currentUser.getUsername());

        User user = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDTO> tasks = taskService.getTasksAssignedToUser(user, pageable)
                .map(taskMapper::toDTO);

        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks created by current user
     * GET /api/tasks/created-by-me
     */
    @GetMapping("/created-by-me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDTO>> getTasksCreatedByMe(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching tasks created by user: {}", currentUser.getUsername());

        User user = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDTO> tasks = taskService.getTasksCreatedByUser(user, pageable)
                .map(taskMapper::toDTO);

        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status
     * GET /api/tasks/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TaskDTO>> getTasksByStatus(
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching tasks by status: {}", status);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDTO> tasks = taskService.getTasksByStatus(status, pageable)
                .map(taskMapper::toDTO);

        return ResponseEntity.ok(tasks);
    }

    /**
     * Assign task to a user
     * PUT /api/tasks/{id}/assign/{userId}
     */
    @PutMapping("/{id}/assign/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Assigning task {} to user {} by {}", id, userId, currentUser.getUsername());

        User assignedBy = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskService.assignTask(id, userId, assignedBy);

        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    /**
     * Update task status
     * PUT /api/tasks/{id}/status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Updating task {} status to {} by {}", id, status, currentUser.getUsername());

        User updatedBy = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskService.updateTaskStatus(id, status, updatedBy);

        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    /**
     * Update task details
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDate dueDate) {

        log.info("Updating task {} by {}", id, currentUser.getUsername());

        User updatedBy = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskService.updateTask(id, title, description, priority, dueDate, updatedBy);

        return ResponseEntity.ok(taskMapper.toDTO(task));
    }

    /**
     * Delete task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Deleting task {} by {}", id, currentUser.getUsername());

        User deletedBy = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        taskService.deleteTask(id, deletedBy);

        return ResponseEntity.ok(new MessageResponse("Task deleted successfully!"));
    }
}
