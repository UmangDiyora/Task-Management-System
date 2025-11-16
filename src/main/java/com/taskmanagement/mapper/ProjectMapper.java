package com.taskmanagement.mapper;

import com.taskmanagement.dto.ProjectDTO;
import com.taskmanagement.entity.Project;
import org.springframework.stereotype.Component;

/**
 * Mapper for Project entity and DTO conversions
 */
@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setOwnerId(project.getOwner().getId());
        dto.setOwnerUsername(project.getOwner().getUsername());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        return dto;
    }
}
