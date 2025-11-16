package com.taskmanagement.mapper;

import com.taskmanagement.dto.NotificationDTO;
import com.taskmanagement.entity.Notification;
import org.springframework.stereotype.Component;

/**
 * Mapper for Notification entity and DTO conversions
 */
@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }
}
