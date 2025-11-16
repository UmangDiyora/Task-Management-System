package com.taskmanagement.repository;

import com.taskmanagement.entity.Notification;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a user
     * @param user the user
     * @param pageable pagination information
     * @return Page of notifications for the user
     */
    Page<Notification> findByUser(User user, Pageable pageable);

    /**
     * Find all notifications for a user ordered by creation date descending
     */
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find all unread notifications for a user
     * @param user the user
     * @param isRead read status
     * @param pageable pagination information
     * @return Page of unread notifications for the user
     */
    Page<Notification> findByUserAndIsRead(User user, Boolean isRead, Pageable pageable);

    /**
     * Find all unread notifications for a user (convenience method)
     * @param user the user
     * @return List of unread notifications
     */
    List<Notification> findByUserAndIsReadFalse(User user);

    /**
     * Count unread notifications for a user
     * @param user the user
     * @param isRead read status
     * @return number of unread notifications
     */
    long countByUserAndIsRead(User user, Boolean isRead);

    /**
     * Count unread notifications for a user (convenience method)
     * @param user the user
     * @return number of unread notifications
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Mark all notifications as read for a user
     * @param user the user
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    /**
     * Delete all read notifications for a user
     * @param user the user
     * @param isRead read status
     */
    void deleteByUserAndIsRead(User user, Boolean isRead);
}
