package com.taskmanagement.util;

public class Constants {

    // API Version
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api";

    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Cache names
    public static final String CACHE_USERS = "users";
    public static final String CACHE_PROJECTS = "projects";
    public static final String CACHE_TASKS = "tasks";

    // Email templates
    public static final String EMAIL_WELCOME = "welcome";
    public static final String EMAIL_TASK_ASSIGNED = "task_assigned";
    public static final String EMAIL_PROJECT_INVITATION = "project_invitation";

    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // WebSocket topics
    public static final String WS_TOPIC_NOTIFICATIONS = "/topic/notifications";
    public static final String WS_QUEUE_NOTIFICATIONS = "/queue/notifications";

    // Error messages
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_PROJECT_NOT_FOUND = "Project not found";
    public static final String ERROR_TASK_NOT_FOUND = "Task not found";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_INVALID_TOKEN = "Invalid or expired token";

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
