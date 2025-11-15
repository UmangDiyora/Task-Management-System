package com.taskmanagement.repository;

import com.taskmanagement.entity.Project;
import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.TaskStatus;
import com.taskmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks in a project
     * @param project the project
     * @param pageable pagination information
     * @return Page of tasks in the project
     */
    Page<Task> findByProject(Project project, Pageable pageable);

    /**
     * Find all tasks assigned to a user
     * @param assignedTo the user assigned to tasks
     * @param pageable pagination information
     * @return Page of tasks assigned to the user
     */
    Page<Task> findByAssignedTo(User assignedTo, Pageable pageable);

    /**
     * Find all tasks created by a user
     * @param createdBy the user who created the tasks
     * @param pageable pagination information
     * @return Page of tasks created by the user
     */
    Page<Task> findByCreatedBy(User createdBy, Pageable pageable);

    /**
     * Find all tasks by status
     * @param status the task status
     * @param pageable pagination information
     * @return Page of tasks with the given status
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    /**
     * Find all tasks in a project by status
     * @param project the project
     * @param status the task status
     * @param pageable pagination information
     * @return Page of tasks matching the criteria
     */
    Page<Task> findByProjectAndStatus(Project project, TaskStatus status, Pageable pageable);

    /**
     * Find all tasks assigned to a user by status
     * @param assignedTo the user assigned to tasks
     * @param status the task status
     * @param pageable pagination information
     * @return Page of tasks matching the criteria
     */
    Page<Task> findByAssignedToAndStatus(User assignedTo, TaskStatus status, Pageable pageable);

    /**
     * Find all tasks in a project
     * @param project the project
     * @return List of tasks in the project
     */
    List<Task> findByProject(Project project);

    /**
     * Count tasks by project and status
     * @param project the project
     * @param status the task status
     * @return number of tasks matching the criteria
     */
    long countByProjectAndStatus(Project project, TaskStatus status);

    /**
     * Find tasks due before a certain date
     * @param dueDate the due date
     * @param status the task status
     * @return List of tasks due before the date
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :dueDate AND t.status = :status")
    List<Task> findTasksDueBefore(@Param("dueDate") LocalDateTime dueDate, @Param("status") TaskStatus status);
}
