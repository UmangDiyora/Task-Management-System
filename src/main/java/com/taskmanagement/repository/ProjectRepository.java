package com.taskmanagement.repository;

import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.ProjectStatus;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Project entity
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find all projects by owner
     * @param owner the project owner
     * @param pageable pagination information
     * @return Page of projects owned by the user
     */
    Page<Project> findByOwner(User owner, Pageable pageable);

    /**
     * Find all projects by status
     * @param status the project status
     * @param pageable pagination information
     * @return Page of projects with the given status
     */
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    /**
     * Find all projects by owner and status
     * @param owner the project owner
     * @param status the project status
     * @param pageable pagination information
     * @return Page of projects matching the criteria
     */
    Page<Project> findByOwnerAndStatus(User owner, ProjectStatus status, Pageable pageable);

    /**
     * Find all projects by owner
     * @param owner the project owner
     * @return List of projects owned by the user
     */
    List<Project> findByOwner(User owner);
}
