package com.taskmanagement.controller;

import com.taskmanagement.dto.CreateProjectRequest;
import com.taskmanagement.dto.MessageResponse;
import com.taskmanagement.dto.ProjectDTO;
import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.ProjectStatus;
import com.taskmanagement.entity.User;
import com.taskmanagement.mapper.ProjectMapper;
import com.taskmanagement.security.UserDetailsImpl;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for project management endpoints
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final ProjectMapper projectMapper;

    /**
     * Create a new project
     * POST /api/projects
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Creating new project: {} by user: {}", request.getName(), currentUser.getUsername());

        User owner = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = projectService.createProject(
                request.getName(),
                request.getDescription(),
                owner,
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectMapper.toDTO(project));
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        log.info("Fetching project by ID: {}", id);

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));

        return ResponseEntity.ok(projectMapper.toDTO(project));
    }

    /**
     * Get all projects with pagination
     * GET /api/projects?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching all projects - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProjectDTO> projects = projectService.getAllProjects(pageable)
                .map(projectMapper::toDTO);

        return ResponseEntity.ok(projects);
    }

    /**
     * Get current user's projects
     * GET /api/projects/my
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProjectDTO>> getMyProjects(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching projects for user: {}", currentUser.getUsername());

        User owner = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProjectDTO> projects = projectService.getProjectsByOwner(owner, pageable)
                .map(projectMapper::toDTO);

        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by status
     * GET /api/projects/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProjectDTO>> getProjectsByStatus(
            @PathVariable ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching projects by status: {}", status);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProjectDTO> projects = projectService.getProjectsByStatus(status, pageable)
                .map(projectMapper::toDTO);

        return ResponseEntity.ok(projects);
    }

    /**
     * Update project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) ProjectStatus status) {

        log.info("Updating project: {} by user: {}", id, currentUser.getUsername());

        // Check if user is the project owner
        if (!projectService.isProjectOwner(id, currentUser.getId())) {
            throw new RuntimeException("You do not have permission to update this project");
        }

        Project updatedProject = projectService.updateProject(id, name, description, status, null, null);

        return ResponseEntity.ok(projectMapper.toDTO(updatedProject));
    }

    /**
     * Delete project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {

        log.info("Deleting project: {} by user: {}", id, currentUser.getUsername());

        // Check if user is the project owner
        if (!projectService.isProjectOwner(id, currentUser.getId())) {
            throw new RuntimeException("You do not have permission to delete this project");
        }

        projectService.deleteProject(id);

        return ResponseEntity.ok(new MessageResponse("Project deleted successfully!"));
    }
}
