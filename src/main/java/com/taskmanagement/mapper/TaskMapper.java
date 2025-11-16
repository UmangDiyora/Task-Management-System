package com.taskmanagement.mapper;

import com.taskmanagement.dto.TaskDTO;
import com.taskmanagement.entity.Task;
import org.springframework.stereotype.Component;

/**
 * Mapper for Task entity and DTO conversions
 */
@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setProjectId(task.getProject().getId());
        dto.setProjectName(task.getProject().getName());
        dto.setCreatedById(task.getCreatedBy().getId());
        dto.setCreatedByUsername(task.getCreatedBy().getUsername());

        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToUsername(task.getAssignedTo().getUsername());
        }

        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        return dto;
    }
}
