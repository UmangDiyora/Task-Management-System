package com.taskmanagement.integration;

import com.taskmanagement.controller.AuthController;
import com.taskmanagement.controller.ProjectController;
import com.taskmanagement.controller.TaskController;
import com.taskmanagement.controller.NotificationController;
import com.taskmanagement.service.AuthService;
import com.taskmanagement.service.TaskService;
import com.taskmanagement.service.ProjectService;
import com.taskmanagement.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke Tests - Critical path verification
 * These tests ensure the application context loads correctly
 * and all critical beans are initialized.
 */
@SpringBootTest
@ActiveProfiles("test")
class SmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthController authController;

    @Autowired
    private TaskController taskController;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private NotificationController notificationController;

    @Autowired
    private AuthService authService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private NotificationService notificationService;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "Application context should load");
    }

    @Test
    void criticalControllersAreLoaded() {
        assertNotNull(authController, "AuthController should be loaded");
        assertNotNull(taskController, "TaskController should be loaded");
        assertNotNull(projectController, "ProjectController should be loaded");
        assertNotNull(notificationController, "NotificationController should be loaded");
    }

    @Test
    void criticalServicesAreLoaded() {
        assertNotNull(authService, "AuthService should be loaded");
        assertNotNull(taskService, "TaskService should be loaded");
        assertNotNull(projectService, "ProjectService should be loaded");
        assertNotNull(notificationService, "NotificationService should be loaded");
    }

    @Test
    void securityBeansAreLoaded() {
        assertTrue(applicationContext.containsBean("securityFilterChain"),
                "Security filter chain should be configured");
        assertTrue(applicationContext.containsBean("passwordEncoder"),
                "Password encoder should be configured");
    }

    @Test
    void jwtUtilityIsLoaded() {
        assertTrue(applicationContext.containsBean("jwtUtil"),
                "JWT utility should be configured");
    }

    @Test
    void mapperConfigurationIsLoaded() {
        assertTrue(applicationContext.containsBean("modelMapper"),
                "ModelMapper should be configured");
        assertTrue(applicationContext.containsBean("entityMapper"),
                "EntityMapper should be configured");
    }

    @Test
    void repositoriesAreLoaded() {
        assertTrue(applicationContext.containsBean("userRepository"),
                "UserRepository should be loaded");
        assertTrue(applicationContext.containsBean("taskRepository"),
                "TaskRepository should be loaded");
        assertTrue(applicationContext.containsBean("projectRepository"),
                "ProjectRepository should be loaded");
        assertTrue(applicationContext.containsBean("notificationRepository"),
                "NotificationRepository should be loaded");
        assertTrue(applicationContext.containsBean("roleRepository"),
                "RoleRepository should be loaded");
    }
}
