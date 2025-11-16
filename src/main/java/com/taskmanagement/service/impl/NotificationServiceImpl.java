package com.taskmanagement.service.impl;

import com.taskmanagement.dto.NotificationDTO;
import com.taskmanagement.entity.Notification;
import com.taskmanagement.entity.NotificationType;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.NotificationMapper;
import com.taskmanagement.repository.NotificationRepository;
import com.taskmanagement.service.NotificationService;
import com.taskmanagement.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Notification Service Implementation
 * Handles notification creation, retrieval, and management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final NotificationMapper notificationMapper;

    /**
     * Create a notification
     */
    @Override
    @Transactional
    public Notification createNotification(User user, String title, String message, NotificationType type) {
        log.info("Creating notification for user: {} - {}", user.getUsername(), title);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with ID: {}", savedNotification.getId());

        // Send real-time notification via WebSocket
        try {
            NotificationDTO notificationDTO = notificationMapper.toDTO(savedNotification);
            webSocketNotificationService.sendNotificationToUser(user.getId(), notificationDTO);
        } catch (Exception e) {
            log.error("Failed to send real-time notification: {}", e.getMessage());
        }

        return savedNotification;
    }

    /**
     * Get all notifications for a user
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        log.debug("Fetching notifications for user: {}", user.getUsername());
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Get unread notifications for a user
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(User user) {
        log.debug("Fetching unread notifications for user: {}", user.getUsername());
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    /**
     * Get unread notification count
     */
    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(User user) {
        log.debug("Counting unread notifications for user: {}", user.getUsername());
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Mark notification as read
     */
    @Override
    @Transactional
    public Notification markAsRead(Long notificationId, User user) {
        log.info("Marking notification {} as read for user: {}", notificationId, user.getUsername());

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found with ID: {}", notificationId);
                    return new RuntimeException("Notification not found with ID: " + notificationId);
                });

        // Verify notification belongs to the user
        if (!notification.getUser().getId().equals(user.getId())) {
            log.error("User {} does not have permission to update notification {}",
                    user.getUsername(), notificationId);
            throw new RuntimeException("You do not have permission to update this notification");
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Notification {} marked as read successfully", notificationId);

        return updatedNotification;
    }

    /**
     * Mark all notifications as read for a user
     */
    @Override
    @Transactional
    public void markAllAsRead(User user) {
        log.info("Marking all notifications as read for user: {}", user.getUsername());

        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalse(user);

        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("Marked {} notifications as read for user: {}",
                unreadNotifications.size(), user.getUsername());
    }

    /**
     * Delete notification
     */
    @Override
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        log.info("Deleting notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.error("Notification not found with ID: {}", notificationId);
                    return new RuntimeException("Notification not found with ID: " + notificationId);
                });

        // Verify notification belongs to the user
        if (!notification.getUser().getId().equals(user.getId())) {
            log.error("User {} does not have permission to delete notification {}",
                    user.getUsername(), notificationId);
            throw new RuntimeException("You do not have permission to delete this notification");
        }

        notificationRepository.deleteById(notificationId);
        log.info("Notification deleted successfully: {}", notificationId);
    }

    /**
     * Delete all read notifications for a user
     */
    @Override
    @Transactional
    public void deleteAllReadNotifications(User user) {
        log.info("Deleting all read notifications for user: {}", user.getUsername());

        List<Notification> readNotifications = notificationRepository.findAll().stream()
                .filter(notification -> notification.getUser().getId().equals(user.getId())
                        && notification.isRead())
                .toList();

        notificationRepository.deleteAll(readNotifications);

        log.info("Deleted {} read notifications for user: {}",
                readNotifications.size(), user.getUsername());
    }
}
