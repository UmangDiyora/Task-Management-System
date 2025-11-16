package com.taskmanagement.dto;

import com.taskmanagement.entity.TaskPriority;
import com.taskmanagement.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Task entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeName;
    private Long createdById;
    private String createdByName;
    private LocalDate dueDate;
    private Long projectId;
    private String projectName;
    private Long createdById;
    private String createdByUsername;
    private Long assignedToId;
    private String assignedToUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
