package com.taskmanagement.service.impl;

import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.ProjectStatus;
import com.taskmanagement.entity.User;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Project Service Implementation
 * Handles CRUD operations for projects
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * Create a new project
     */
    @Override
    @Transactional
    public Project createProject(String name, String description, User owner,
                                LocalDate startDate, LocalDate endDate) {
        log.info("Creating new project: {} for owner: {}", name, owner.getUsername());

        // Validate dates
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            log.error("End date cannot be before start date");
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setOwner(owner);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(startDate);
        project.setEndDate(endDate);

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}", savedProject.getId());

        return savedProject;
    }

    /**
     * Get project by ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(Long projectId) {
        log.debug("Fetching project by ID: {}", projectId);
        return projectRepository.findById(projectId);
    }

    /**
     * Get all projects with pagination
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(Pageable pageable) {
        log.debug("Fetching all projects with pagination: {}", pageable);
        return projectRepository.findAll(pageable);
    }

    /**
     * Get projects by owner
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Project> getProjectsByOwner(User owner, Pageable pageable) {
        log.debug("Fetching projects for owner: {}", owner.getUsername());
        return projectRepository.findByOwner(owner, pageable);
    }

    /**
     * Get projects by status
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Project> getProjectsByStatus(ProjectStatus status, Pageable pageable) {
        log.debug("Fetching projects with status: {}", status);
        return projectRepository.findByStatus(status, pageable);
    }

    /**
     * Update project
     */
    @Override
    @Transactional
    public Project updateProject(Long projectId, String name, String description,
                                ProjectStatus status, LocalDate startDate, LocalDate endDate) {
        log.info("Updating project: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found with ID: {}", projectId);
                    return new RuntimeException("Project not found with ID: " + projectId);
                });

        // Update fields if provided
        if (name != null && !name.isEmpty()) {
            project.setName(name);
        }

        if (description != null) {
            project.setDescription(description);
        }

        if (status != null) {
            project.setStatus(status);
        }

        if (startDate != null) {
            project.setStartDate(startDate);
        }

        if (endDate != null) {
            project.setEndDate(endDate);
        }

        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null
                && project.getEndDate().isBefore(project.getStartDate())) {
            log.error("End date cannot be before start date");
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully: {}", updatedProject.getId());

        return updatedProject;
    }

    /**
     * Delete project
     */
    @Override
    @Transactional
    public void deleteProject(Long projectId) {
        log.info("Deleting project: {}", projectId);

        if (!projectRepository.existsById(projectId)) {
            log.error("Project not found with ID: {}", projectId);
            throw new RuntimeException("Project not found with ID: " + projectId);
        }

        projectRepository.deleteById(projectId);
        log.info("Project deleted successfully: {}", projectId);
    }

    /**
     * Check if user owns project
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isProjectOwner(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElse(null);

        if (project == null) {
            return false;
        }

        return project.getOwner().getId().equals(userId);
    }

    /**
     * Get project statistics for owner
     */
    @Override
    @Transactional(readOnly = true)
    public List<Project> getProjectStatistics(Long ownerId) {
        log.debug("Fetching project statistics for owner: {}", ownerId);
        return projectRepository.findAll().stream()
                .filter(project -> project.getOwner().getId().equals(ownerId))
                .toList();
    }
}
