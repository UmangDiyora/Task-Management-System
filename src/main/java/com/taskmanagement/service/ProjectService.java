package com.taskmanagement.service;

import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.ProjectStatus;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Project Service Interface
 * Handles CRUD operations for projects
 */
public interface ProjectService {

    /**
     * Create a new project
     * @param name Project name
     * @param description Project description
     * @param owner Project owner
     * @param startDate Start date
     * @param endDate End date
     * @return Created project
     */
    Project createProject(String name, String description, User owner, LocalDate startDate, LocalDate endDate);

    /**
     * Get project by ID
     * @param projectId Project ID
     * @return Project if found
     */
    Optional<Project> getProjectById(Long projectId);

    /**
     * Get all projects with pagination
     * @param pageable Pagination parameters
     * @return Page of projects
     */
    Page<Project> getAllProjects(Pageable pageable);

    /**
     * Get projects by owner
     * @param owner Project owner
     * @param pageable Pagination parameters
     * @return Page of projects
     */
    Page<Project> getProjectsByOwner(User owner, Pageable pageable);

    /**
     * Get projects by status
     * @param status Project status
     * @param pageable Pagination parameters
     * @return Page of projects
     */
    Page<Project> getProjectsByStatus(ProjectStatus status, Pageable pageable);

    /**
     * Update project
     * @param projectId Project ID
     * @param name New name (optional)
     * @param description New description (optional)
     * @param status New status (optional)
     * @param startDate New start date (optional)
     * @param endDate New end date (optional)
     * @return Updated project
     */
    Project updateProject(Long projectId, String name, String description,
                         ProjectStatus status, LocalDate startDate, LocalDate endDate);

    /**
     * Delete project
     * @param projectId Project ID
     */
    void deleteProject(Long projectId);

    /**
     * Check if user owns project
     * @param projectId Project ID
     * @param userId User ID
     * @return true if user owns project, false otherwise
     */
    boolean isProjectOwner(Long projectId, Long userId);

    /**
     * Get project statistics for owner
     * @param ownerId Owner user ID
     * @return List of projects with statistics
     */
    List<Project> getProjectStatistics(Long ownerId);
}
