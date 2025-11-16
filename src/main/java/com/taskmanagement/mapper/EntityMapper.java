package com.taskmanagement.mapper;

import com.taskmanagement.dto.*;
import com.taskmanagement.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    private final ModelMapper modelMapper;

    public EntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // User mappings
    public UserDTO toUserDTO(User user) {
        if (user == null) return null;
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        return dto;
    }

    public User toUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    // Task mappings
    public TaskDTO toTaskDTO(Task task) {
        if (task == null) return null;
        TaskDTO dto = modelMapper.map(task, TaskDTO.class);
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
            dto.setProjectName(task.getProject().getName());
        }
        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());
            dto.setAssigneeName(task.getAssignee().getUsername());
        }
        if (task.getCreatedBy() != null) {
            dto.setCreatedById(task.getCreatedBy().getId());
            dto.setCreatedByName(task.getCreatedBy().getUsername());
        }
        return dto;
    }

    public Task toTask(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    // Project mappings
    public ProjectDTO toProjectDTO(Project project) {
        if (project == null) return null;
        ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
        if (project.getOwner() != null) {
            dto.setOwnerId(project.getOwner().getId());
            dto.setOwnerName(project.getOwner().getUsername());
        }
        if (project.getTeamMembers() != null) {
            dto.setTeamMemberIds(project.getTeamMembers().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet()));
            dto.setTeamMemberNames(project.getTeamMembers().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toSet()));
        }
        if (project.getTasks() != null) {
            dto.setTaskCount(project.getTasks().size());
        }
        return dto;
    }

    public Project toProject(ProjectDTO projectDTO) {
        return modelMapper.map(projectDTO, Project.class);
    }

    // Notification mappings
    public NotificationDTO toNotificationDTO(Notification notification) {
        if (notification == null) return null;
        NotificationDTO dto = modelMapper.map(notification, NotificationDTO.class);
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
        }
        if (notification.getRelatedTask() != null) {
            dto.setRelatedTaskId(notification.getRelatedTask().getId());
        }
        if (notification.getRelatedProject() != null) {
            dto.setRelatedProjectId(notification.getRelatedProject().getId());
        }
        return dto;
    }

    public Notification toNotification(NotificationDTO notificationDTO) {
        return modelMapper.map(notificationDTO, Notification.class);
    }
}
