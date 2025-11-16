package com.taskmanagement.service;

import com.taskmanagement.entity.Notification;
import com.taskmanagement.entity.NotificationType;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Notification Service Interface
 * Handles notification creation, retrieval, and management
 */
public interface NotificationService {

    /**
     * Create a notification
     * @param user User to notify
     * @param title Notification title
     * @param message Notification message
     * @param type Notification type
     * @return Created notification
     */
    Notification createNotification(User user, String title, String message, NotificationType type);

    /**
     * Get all notifications for a user
     * @param user User
     * @param pageable Pagination parameters
     * @return Page of notifications
     */
    Page<Notification> getUserNotifications(User user, Pageable pageable);

    /**
     * Get unread notifications for a user
     * @param user User
     * @return List of unread notifications
     */
    List<Notification> getUnreadNotifications(User user);

    /**
     * Get unread notification count for a user
     * @param user User
     * @return Count of unread notifications
     */
    long getUnreadNotificationCount(User user);

    /**
     * Mark notification as read
     * @param notificationId Notification ID
     * @param user User (for verification)
     * @return Updated notification
     */
    Notification markAsRead(Long notificationId, User user);

    /**
     * Mark all notifications as read for a user
     * @param user User
     */
    void markAllAsRead(User user);

    /**
     * Delete notification
     * @param notificationId Notification ID
     * @param user User (for verification)
     */
    void deleteNotification(Long notificationId, User user);

    /**
     * Delete all read notifications for a user
     * @param user User
     */
    void deleteAllReadNotifications(User user);
}
