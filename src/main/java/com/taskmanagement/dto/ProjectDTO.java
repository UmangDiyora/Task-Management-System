package com.taskmanagement.dto;

import com.taskmanagement.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Project entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long ownerId;
    private String ownerName;
    private Set<Long> teamMemberIds;
    private Set<String> teamMemberNames;
    private Integer taskCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
