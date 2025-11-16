package com.taskmanagement.service;

import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.User;

/**
 * Email Service Interface
 * Handles sending various types of emails
 */
public interface EmailService {

    /**
     * Send welcome email to newly registered user
     * @param user New user
     */
    void sendWelcomeEmail(User user);

    /**
     * Send task assignment notification email
     * @param task Assigned task
     * @param assignee User assigned to the task
     */
    void sendTaskAssignmentEmail(Task task, User assignee);

    /**
     * Send task update notification email
     * @param task Updated task
     * @param recipient Recipient user
     * @param updateMessage Update message
     */
    void sendTaskUpdateEmail(Task task, User recipient, String updateMessage);

    /**
     * Send generic notification email
     * @param recipient Recipient email address
     * @param subject Email subject
     * @param body Email body
     */
    void sendEmail(String recipient, String subject, String body);
}
