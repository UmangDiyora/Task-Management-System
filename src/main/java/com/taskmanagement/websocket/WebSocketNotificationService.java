package com.taskmanagement.websocket;

import com.taskmanagement.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending real-time notifications via WebSocket
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send notification to a specific user
     * @param userId User ID to send notification to
     * @param notification Notification DTO
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        log.info("Sending WebSocket notification to user {}: {}", userId, notification.getTitle());

        try {
            // Send to user-specific queue
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );

            log.debug("Notification sent successfully to user {}", userId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Send notification to all users (broadcast)
     * @param notification Notification DTO
     */
    public void broadcastNotification(NotificationDTO notification) {
        log.info("Broadcasting WebSocket notification: {}", notification.getTitle());

        try {
            // Send to topic for all subscribers
            messagingTemplate.convertAndSend(
                    "/topic/notifications",
                    notification
            );

            log.debug("Notification broadcasted successfully");
        } catch (Exception e) {
            log.error("Failed to broadcast WebSocket notification: {}", e.getMessage());
        }
    }

    /**
     * Send task update notification
     * @param projectId Project ID
     * @param taskId Task ID
     * @param message Update message
     */
    public void sendTaskUpdate(Long projectId, Long taskId, String message) {
        log.info("Sending task update for project {} task {}: {}", projectId, taskId, message);

        try {
            // Send to project-specific topic
            messagingTemplate.convertAndSend(
                    "/topic/projects/" + projectId + "/tasks",
                    message
            );

            log.debug("Task update sent successfully");
        } catch (Exception e) {
            log.error("Failed to send task update: {}", e.getMessage());
        }
    }

    /**
     * Send project update notification
     * @param projectId Project ID
     * @param message Update message
     */
    public void sendProjectUpdate(Long projectId, String message) {
        log.info("Sending project update for project {}: {}", projectId, message);

        try {
            // Send to project-specific topic
            messagingTemplate.convertAndSend(
                    "/topic/projects/" + projectId,
                    message
            );

            log.debug("Project update sent successfully");
        } catch (Exception e) {
            log.error("Failed to send project update: {}", e.getMessage());
        }
    }
}
