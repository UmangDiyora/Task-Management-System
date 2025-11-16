package com.taskmanagement.service;

import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Task Service Interface
 * Handles CRUD operations for tasks with business logic validation
 */
public interface TaskService {

    /**
     * Create a new task
     * @param title Task title
     * @param description Task description
     * @param projectId Project ID
     * @param createdBy User creating the task
     * @param priority Task priority
     * @param dueDate Due date
     * @return Created task
     */
    Task createTask(String title, String description, Long projectId,
                   User createdBy, TaskPriority priority, LocalDate dueDate);

    /**
     * Assign task to a user
     * @param taskId Task ID
     * @param assigneeId User ID to assign to
     * @param assignedBy User assigning the task
     * @return Updated task
     */
    Task assignTask(Long taskId, Long assigneeId, User assignedBy);

    /**
     * Update task status
     * @param taskId Task ID
     * @param status New status
     * @param updatedBy User updating the status
     * @return Updated task
     */
    Task updateTaskStatus(Long taskId, TaskStatus status, User updatedBy);

    /**
     * Update task
     * @param taskId Task ID
     * @param title New title (optional)
     * @param description New description (optional)
     * @param priority New priority (optional)
     * @param dueDate New due date (optional)
     * @param updatedBy User updating the task
     * @return Updated task
     */
    Task updateTask(Long taskId, String title, String description,
                   TaskPriority priority, LocalDate dueDate, User updatedBy);

    /**
     * Get task by ID
     * @param taskId Task ID
     * @return Task if found
     */
    Optional<Task> getTaskById(Long taskId);

    /**
     * Get all tasks with pagination
     * @param pageable Pagination parameters
     * @return Page of tasks
     */
    Page<Task> getAllTasks(Pageable pageable);

    /**
     * Get tasks by project
     * @param projectId Project ID
     * @param pageable Pagination parameters
     * @return Page of tasks
     */
    Page<Task> getTasksByProject(Long projectId, Pageable pageable);

    /**
     * Get tasks assigned to user
     * @param assignee User
     * @param pageable Pagination parameters
     * @return Page of tasks
     */
    Page<Task> getTasksAssignedToUser(User assignee, Pageable pageable);

    /**
     * Get tasks created by user
     * @param createdBy User
     * @param pageable Pagination parameters
     * @return Page of tasks
     */
    Page<Task> getTasksCreatedByUser(User createdBy, Pageable pageable);

    /**
     * Get tasks by status
     * @param status Task status
     * @param pageable Pagination parameters
     * @return Page of tasks
     */
    Page<Task> getTasksByStatus(TaskStatus status, Pageable pageable);

    /**
     * Delete task
     * @param taskId Task ID
     * @param deletedBy User deleting the task
     */
    void deleteTask(Long taskId, User deletedBy);

    /**
     * Check if user can modify task
     * @param taskId Task ID
     * @param userId User ID
     * @return true if user can modify, false otherwise
     */
    boolean canUserModifyTask(Long taskId, Long userId);
}
