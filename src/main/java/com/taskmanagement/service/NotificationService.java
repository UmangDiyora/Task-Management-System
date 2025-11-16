package com.taskmanagement.service;

import com.taskmanagement.dto.NotificationDTO;
import com.taskmanagement.entity.Notification;
import com.taskmanagement.entity.NotificationType;
import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.EntityMapper;
import com.taskmanagement.repository.NotificationRepository;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final EntityMapper entityMapper;
    private SimpMessagingTemplate messagingTemplate;  // Will be injected later for WebSocket

    public NotificationService(NotificationRepository notificationRepository,
                              UserRepository userRepository,
                              TaskRepository taskRepository,
                              ProjectRepository projectRepository,
                              EntityMapper entityMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.entityMapper = entityMapper;
    }

    // Setter for WebSocket template (to avoid circular dependency)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public NotificationDTO createNotification(Long userId, String message, NotificationType type,
                                             Long taskId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setUser(user);

        if (taskId != null) {
            Task task = taskRepository.findById(taskId).orElse(null);
            notification.setRelatedTask(task);
        }

        if (projectId != null) {
            Project project = projectRepository.findById(projectId).orElse(null);
            notification.setRelatedProject(project);
        }

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO notificationDTO = entityMapper.toNotificationDTO(savedNotification);

        // Send real-time notification via WebSocket if template is available
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/notifications",
                    notificationDTO
            );
        }

        return notificationDTO;
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(entityMapper::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(entityMapper::toNotificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return entityMapper.toNotificationDTO(updatedNotification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.delete(notification);
    }
}
