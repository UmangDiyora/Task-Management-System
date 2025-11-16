package com.taskmanagement.service;

import com.taskmanagement.dto.TaskDTO;
import com.taskmanagement.entity.*;
import com.taskmanagement.mapper.EntityMapper;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository,
                      ProjectRepository projectRepository,
                      UserRepository userRepository,
                      EntityMapper entityMapper,
                      NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, Long createdById) {
        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.TODO);
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(taskDTO.getDueDate());
        task.setProject(project);
        task.setCreatedBy(createdBy);

        if (taskDTO.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskDTO.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found"));
            task.setAssignee(assignee);

            // Send notification to assignee
            notificationService.createNotification(
                    assignee.getId(),
                    "You have been assigned a new task: " + task.getTitle(),
                    NotificationType.TASK_ASSIGNED,
                    task.getId(),
                    project.getId()
            );
        }

        Task savedTask = taskRepository.save(task);
        return entityMapper.toTaskDTO(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return entityMapper.toTaskDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(entityMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(entityMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByAssignee(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId).stream()
                .map(entityMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(entityMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setDueDate(taskDTO.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return entityMapper.toTaskDTO(updatedTask);
    }

    @Transactional
    public TaskDTO updateTaskStatus(Long id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        TaskStatus oldStatus = task.getStatus();
        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);

        // Notify assignee about status change
        if (task.getAssignee() != null) {
            notificationService.createNotification(
                    task.getAssignee().getId(),
                    "Task '" + task.getTitle() + "' status changed from " + oldStatus + " to " + newStatus,
                    NotificationType.TASK_UPDATED,
                    task.getId(),
                    task.getProject().getId()
            );
        }

        return entityMapper.toTaskDTO(updatedTask);
    }

    @Transactional
    public TaskDTO assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + assigneeId));

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);

        // Send notification to new assignee
        notificationService.createNotification(
                assignee.getId(),
                "You have been assigned to task: " + task.getTitle(),
                NotificationType.TASK_ASSIGNED,
                task.getId(),
                task.getProject().getId()
        );

        return entityMapper.toTaskDTO(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
}
