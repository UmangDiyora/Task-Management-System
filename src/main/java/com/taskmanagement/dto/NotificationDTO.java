package com.taskmanagement.dto;

import com.taskmanagement.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private Boolean read;
    private Long userId;
    private Long relatedTaskId;
    private Long relatedProjectId;
    private LocalDateTime createdAt;
}
