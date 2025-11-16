package com.taskmanagement.service;

import com.taskmanagement.dto.ProjectDTO;
import com.taskmanagement.entity.NotificationType;
import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.ProjectStatus;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.EntityMapper;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final NotificationService notificationService;

    public ProjectService(ProjectRepository projectRepository,
                         UserRepository userRepository,
                         EntityMapper entityMapper,
                         NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.entityMapper = entityMapper;
        this.notificationService = notificationService;
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus() != null ? projectDTO.getStatus() : ProjectStatus.PLANNING);
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());
        project.setOwner(owner);

        // Initialize team members with owner
        Set<User> teamMembers = new HashSet<>();
        teamMembers.add(owner);
        project.setTeamMembers(teamMembers);

        Project savedProject = projectRepository.save(project);
        return entityMapper.toProjectDTO(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return entityMapper.toProjectDTO(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(entityMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByOwner(Long ownerId) {
        return projectRepository.findByOwnerId(ownerId).stream()
                .map(entityMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByTeamMember(Long userId) {
        return projectRepository.findByTeamMembersId(userId).stream()
                .map(entityMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
                .map(entityMapper::toProjectDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setStatus(projectDTO.getStatus());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        Project updatedProject = projectRepository.save(project);
        return entityMapper.toProjectDTO(updatedProject);
    }

    @Transactional
    public ProjectDTO addTeamMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getTeamMembers().add(user);
        Project updatedProject = projectRepository.save(project);

        // Notify user about being added to project
        notificationService.createNotification(
                userId,
                "You have been added to project: " + project.getName(),
                NotificationType.PROJECT_UPDATED,
                null,
                projectId
        );

        return entityMapper.toProjectDTO(updatedProject);
    }

    @Transactional
    public ProjectDTO removeTeamMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getTeamMembers().remove(user);
        Project updatedProject = projectRepository.save(project);

        return entityMapper.toProjectDTO(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        projectRepository.delete(project);
    }
}
