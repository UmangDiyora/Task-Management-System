package com.taskmanagement.repository;

import com.taskmanagement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Project project;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        // Create and persist user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFullName("Test User");
        user = entityManager.persist(user);

        // Create and persist project
        project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setOwner(user);
        project = entityManager.persist(project);

        // Create and persist tasks
        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskStatus.TODO);
        task1.setPriority(TaskPriority.HIGH);
        task1.setProject(project);
        task1.setCreatedBy(user);
        task1.setAssignee(user);
        task1 = entityManager.persist(task1);

        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setPriority(TaskPriority.LOW);
        task2.setProject(project);
        task2.setCreatedBy(user);
        task2 = entityManager.persist(task2);

        entityManager.flush();
    }

    @Test
    void testFindByProjectId() {
        List<Task> tasks = taskRepository.findByProjectId(project.getId());

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void testFindByAssigneeId() {
        List<Task> tasks = taskRepository.findByAssigneeId(user.getId());

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }

    @Test
    void testFindByStatus() {
        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
        List<Task> inProgressTasks = taskRepository.findByStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, todoTasks.size());
        assertEquals(1, inProgressTasks.size());
        assertEquals("Task 1", todoTasks.get(0).getTitle());
        assertEquals("Task 2", inProgressTasks.get(0).getTitle());
    }

    @Test
    void testFindByProjectIdAndStatus() {
        List<Task> tasks = taskRepository.findByProjectIdAndStatus(project.getId(), TaskStatus.TODO);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }

    @Test
    void testFindByAssigneeIdAndStatus() {
        List<Task> tasks = taskRepository.findByAssigneeIdAndStatus(user.getId(), TaskStatus.TODO);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }

    @Test
    void testCountByProjectId() {
        long count = taskRepository.countByProjectId(project.getId());
        assertEquals(2, count);
    }

    @Test
    void testCountByAssigneeIdAndStatus() {
        long count = taskRepository.countByAssigneeIdAndStatus(user.getId(), TaskStatus.TODO);
        assertEquals(1, count);
    }
}
