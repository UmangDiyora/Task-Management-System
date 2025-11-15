package com.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for Task Management System
 *
 * Features:
 * - JWT Authentication & Authorization
 * - Real-time WebSocket Notifications
 * - Redis Caching
 * - Email Notifications
 * - Role-based Access Control
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class TaskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApplication.class, args);
    }

}
