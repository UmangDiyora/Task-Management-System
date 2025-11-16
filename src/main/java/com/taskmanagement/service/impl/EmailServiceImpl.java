package com.taskmanagement.service.impl;

import com.taskmanagement.entity.Task;
import com.taskmanagement.entity.User;
import com.taskmanagement.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email Service Implementation
 * Handles sending emails asynchronously with retry logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * Send welcome email to newly registered user
     */
    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        log.info("Sending welcome email to: {}", user.getEmail());

        String subject = "Welcome to Task Management System";
        String body = String.format(
                "Hi %s,\n\n" +
                        "Welcome to Task Management System!\n\n" +
                        "Your account has been successfully created with username: %s\n\n" +
                        "You can now:\n" +
                        "- Create and manage projects\n" +
                        "- Create and assign tasks\n" +
                        "- Collaborate with team members\n" +
                        "- Track project progress\n\n" +
                        "Get started by logging in and creating your first project!\n\n" +
                        "Best regards,\n" +
                        "Task Management Team",
                user.getFullName(),
                user.getUsername()
        );

        sendEmailWithRetry(user.getEmail(), subject, body);
    }

    /**
     * Send task assignment notification email
     */
    @Override
    @Async
    public void sendTaskAssignmentEmail(Task task, User assignee) {
        log.info("Sending task assignment email to: {}", assignee.getEmail());

        String subject = "New Task Assigned: " + task.getTitle();
        String body = String.format(
                "Hi %s,\n\n" +
                        "You have been assigned a new task:\n\n" +
                        "Task: %s\n" +
                        "Description: %s\n" +
                        "Priority: %s\n" +
                        "Status: %s\n" +
                        "Project: %s\n" +
                        "Due Date: %s\n\n" +
                        "Please log in to the Task Management System to view the full details.\n\n" +
                        "Best regards,\n" +
                        "Task Management Team",
                assignee.getFullName(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getProject().getName(),
                task.getDueDate() != null ? task.getDueDate().toString() : "Not set"
        );

        sendEmailWithRetry(assignee.getEmail(), subject, body);
    }

    /**
     * Send task update notification email
     */
    @Override
    @Async
    public void sendTaskUpdateEmail(Task task, User recipient, String updateMessage) {
        log.info("Sending task update email to: {}", recipient.getEmail());

        String subject = "Task Updated: " + task.getTitle();
        String body = String.format(
                "Hi %s,\n\n" +
                        "A task has been updated:\n\n" +
                        "Task: %s\n" +
                        "Update: %s\n" +
                        "Current Status: %s\n" +
                        "Priority: %s\n" +
                        "Project: %s\n\n" +
                        "Please log in to the Task Management System to view the full details.\n\n" +
                        "Best regards,\n" +
                        "Task Management Team",
                recipient.getFullName(),
                task.getTitle(),
                updateMessage,
                task.getStatus(),
                task.getPriority(),
                task.getProject().getName()
        );

        sendEmailWithRetry(recipient.getEmail(), subject, body);
    }

    /**
     * Send generic email
     */
    @Override
    @Async
    public void sendEmail(String recipient, String subject, String body) {
        log.info("Sending email to: {}", recipient);
        sendEmailWithRetry(recipient, subject, body);
    }

    /**
     * Send email with retry logic
     * Retries up to 3 times with 2 second delay between attempts
     */
    private void sendEmailWithRetry(String to, String subject, String body) {
        int maxRetries = 3;
        int retryCount = 0;
        boolean sent = false;

        while (!sent && retryCount < maxRetries) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);

                mailSender.send(message);
                sent = true;
                log.info("Email sent successfully to: {}", to);

            } catch (MailException e) {
                retryCount++;
                log.warn("Failed to send email to {} (attempt {}/{}): {}",
                        to, retryCount, maxRetries, e.getMessage());

                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Email retry interrupted", ie);
                        break;
                    }
                } else {
                    log.error("Failed to send email to {} after {} attempts", to, maxRetries);
                }
            }
        }
    }
}
