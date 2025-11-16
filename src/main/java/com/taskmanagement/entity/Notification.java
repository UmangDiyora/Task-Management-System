package com.taskmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a notification for a user
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @NotBlank(message = "Notification message is required")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @NotBlank(message = "Notification title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public Notification(NotificationType type, String message, User user) {
        this.type = type;
        this.message = message;
        this.user = user;
        this.isRead = false;
    }

    public Notification(NotificationType type, String message, User user, Long referenceId, String referenceType) {
        this.type = type;
        this.message = message;
        this.user = user;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.isRead = false;
    }
}
