package com.taskmanagement.service.impl;

import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.entity.User;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Task Service Implementation
 * Handles CRUD operations for tasks with comprehensive business logic validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    // NotificationService will be injected later when we create it

    /**
     * Create a new task with validation
     */
    @Override
    @Transactional
    public Task createTask(String title, String description, Long projectId,
                          User createdBy, TaskPriority priority, LocalDate dueDate) {
        log.info("Creating new task: {} for project: {}", title, projectId);

        // Validate project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found with ID: {}", projectId);
                    return new RuntimeException("Project not found with ID: " + projectId);
                });

        // Verify user has permission to create tasks in this project
        // (only project owner can create tasks)
        if (!project.getOwner().getId().equals(createdBy.getId())) {
            log.error("User {} does not have permission to create tasks in project {}",
                    createdBy.getUsername(), projectId);
            throw new RuntimeException("You do not have permission to create tasks in this project");
        }

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setProject(project);
        task.setCreatedBy(createdBy);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(priority != null ? priority : TaskPriority.MEDIUM);
        task.setDueDate(dueDate);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return savedTask;
    }

    /**
     * Assign task to a user with validation
     */
    @Override
    @Transactional
    public Task assignTask(Long taskId, Long assigneeId, User assignedBy) {
        log.info("Assigning task {} to user {}", taskId, assigneeId);

        // Validate task exists
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with ID: {}", taskId);
                    return new RuntimeException("Task not found with ID: " + taskId);
                });

        // Validate assignee exists
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", assigneeId);
                    return new RuntimeException("User not found with ID: " + assigneeId);
                });

        // Verify permission (only project owner or task creator can assign)
        if (!canUserModifyTask(taskId, assignedBy.getId())) {
            log.error("User {} does not have permission to assign task {}",
                    assignedBy.getUsername(), taskId);
            throw new RuntimeException("You do not have permission to assign this task");
        }

        task.setAssignedTo(assignee);

        // Update status to IN_PROGRESS if it was TODO
        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task {} assigned to user {} successfully", taskId, assignee.getUsername());

        // TODO: Create notification for assignee (will be implemented with NotificationService)
        // notificationService.createNotification(assignee, "Task Assigned",
        //     "You have been assigned to task: " + task.getTitle());

        return updatedTask;
    }

    /**
     * Update task status with validation
     */
    @Override
    @Transactional
    public Task updateTaskStatus(Long taskId, TaskStatus status, User updatedBy) {
        log.info("Updating task {} status to {}", taskId, status);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with ID: {}", taskId);
                    return new RuntimeException("Task not found with ID: " + taskId);
                });

        // Verify permission (assignee, creator, or project owner can update status)
        if (!canUserModifyTask(taskId, updatedBy.getId())
                && (task.getAssignedTo() == null || !task.getAssignedTo().getId().equals(updatedBy.getId()))) {
            log.error("User {} does not have permission to update task {}",
                    updatedBy.getUsername(), taskId);
            throw new RuntimeException("You do not have permission to update this task");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        log.info("Task {} status updated to {} successfully", taskId, status);

        // TODO: Create notification for task creator and project owner
        // if (!updatedBy.getId().equals(task.getCreatedBy().getId())) {
        //     notificationService.createNotification(task.getCreatedBy(), "Task Updated",
        //         "Task '" + task.getTitle() + "' status changed to " + status);
        // }

        return updatedTask;
    }

    /**
     * Update task details
     */
    @Override
    @Transactional
    public Task updateTask(Long taskId, String title, String description,
                          TaskPriority priority, LocalDate dueDate, User updatedBy) {
        log.info("Updating task: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with ID: {}", taskId);
                    return new RuntimeException("Task not found with ID: " + taskId);
                });

        // Verify permission
        if (!canUserModifyTask(taskId, updatedBy.getId())) {
            log.error("User {} does not have permission to update task {}",
                    updatedBy.getUsername(), taskId);
            throw new RuntimeException("You do not have permission to update this task");
        }

        // Update fields if provided
        if (title != null && !title.isEmpty()) {
            task.setTitle(title);
        }

        if (description != null) {
            task.setDescription(description);
        }

        if (priority != null) {
            task.setPriority(priority);
        }

        if (dueDate != null) {
            task.setDueDate(dueDate);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getId());

        return updatedTask;
    }

    /**
     * Get task by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        log.debug("Fetching task by ID: {}", taskId);
        return taskRepository.findById(taskId);
    }

    /**
     * Get all tasks with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getAllTasks(Pageable pageable) {
        log.debug("Fetching all tasks with pagination: {}", pageable);
        return taskRepository.findAll(pageable);
    }

    /**
     * Get tasks by project
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksByProject(Long projectId, Pageable pageable) {
        log.debug("Fetching tasks for project: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        return taskRepository.findByProject(project, pageable);
    }

    /**
     * Get tasks assigned to user
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksAssignedToUser(User assignee, Pageable pageable) {
        log.debug("Fetching tasks assigned to user: {}", assignee.getUsername());
        return taskRepository.findByAssignedTo(assignee, pageable);
    }

    /**
     * Get tasks created by user
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksCreatedByUser(User createdBy, Pageable pageable) {
        log.debug("Fetching tasks created by user: {}", createdBy.getUsername());
        return taskRepository.findByCreatedBy(createdBy, pageable);
    }

    /**
     * Get tasks by status
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasksByStatus(TaskStatus status, Pageable pageable) {
        log.debug("Fetching tasks with status: {}", status);
        return taskRepository.findByStatus(status, pageable);
    }

    /**
     * Delete task
     */
    @Override
    @Transactional
    public void deleteTask(Long taskId, User deletedBy) {
        log.info("Deleting task: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with ID: {}", taskId);
                    return new RuntimeException("Task not found with ID: " + taskId);
                });

        // Verify permission (only project owner or task creator can delete)
        if (!canUserModifyTask(taskId, deletedBy.getId())) {
            log.error("User {} does not have permission to delete task {}",
                    deletedBy.getUsername(), taskId);
            throw new RuntimeException("You do not have permission to delete this task");
        }

        taskRepository.deleteById(taskId);
        log.info("Task deleted successfully: {}", taskId);
    }

    /**
     * Check if user can modify task
     * User can modify if they are:
     * - The project owner
     * - The task creator
     */
    @Override
    @Transactional(readOnly = true)
    public boolean canUserModifyTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElse(null);

        if (task == null) {
            return false;
        }

        // User is the task creator
        if (task.getCreatedBy().getId().equals(userId)) {
            return true;
        }

        // User is the project owner
        if (task.getProject().getOwner().getId().equals(userId)) {
            return true;
        }

        return false;
    }
}
