package com.taskmanagement.service;

import com.taskmanagement.dto.TaskDTO;
import com.taskmanagement.entity.*;
import com.taskmanagement.mapper.EntityMapper;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setOwner(user);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(project);
        task.setCreatedBy(user);
        task.setAssignee(user);

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setStatus(TaskStatus.TODO);
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setProjectId(1L);
    }

    @Test
    void testCreateTask_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.createTask(taskDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTask_WithAssignee() {
        // Arrange
        taskDTO.setAssigneeId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.createTask(taskDTO, 1L);

        // Assert
        assertNotNull(result);
        verify(notificationService, times(1)).createNotification(
                anyLong(), anyString(), any(NotificationType.class), anyLong(), anyLong());
    }

    @Test
    void testCreateTask_UserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(taskDTO, 1L);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetTaskById_Success() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(1L);
        });
    }

    @Test
    void testGetAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testUpdateTaskStatus_Success() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).save(task);
        verify(notificationService, times(1)).createNotification(
                anyLong(), anyString(), any(NotificationType.class), anyLong(), anyLong());
    }

    @Test
    void testAssignTask_Success() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        TaskDTO result = taskService.assignTask(1L, 1L);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).save(task);
        verify(notificationService, times(1)).createNotification(
                anyLong(), anyString(), any(NotificationType.class), anyLong(), anyLong());
    }

    @Test
    void testDeleteTask_Success() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(any(Task.class));

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testDeleteTask_NotFound() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.deleteTask(1L);
        });
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void testGetTasksByProject() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByProjectId(anyLong())).thenReturn(tasks);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.getTasksByProject(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByProjectId(1L);
    }

    @Test
    void testGetTasksByAssignee() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByAssigneeId(anyLong())).thenReturn(tasks);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.getTasksByAssignee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByAssigneeId(1L);
    }

    @Test
    void testGetTasksByStatus() {
        // Arrange
        List<Task> tasks = Arrays.asList(task);
        when(taskRepository.findByStatus(any(TaskStatus.class))).thenReturn(tasks);
        when(entityMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);

        // Act
        List<TaskDTO> result = taskService.getTasksByStatus(TaskStatus.TODO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByStatus(TaskStatus.TODO);
    }
}
