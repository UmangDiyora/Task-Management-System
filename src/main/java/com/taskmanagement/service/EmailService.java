package com.taskmanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@taskmanagement.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Task Management System");
        message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to Task Management System! Your account has been successfully created.\n\n" +
                "You can now start creating projects, managing tasks, and collaborating with your team.\n\n" +
                "Best regards,\n" +
                "Task Management Team",
                username
        ));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    public void sendTaskAssignmentEmail(String toEmail, String taskTitle, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("New Task Assigned: " + taskTitle);
        message.setText(String.format(
                "Hello,\n\n" +
                "You have been assigned a new task:\n" +
                "Task: %s\n" +
                "Project: %s\n\n" +
                "Please log in to the Task Management System to view details and manage this task.\n\n" +
                "Best regards,\n" +
                "Task Management Team",
                taskTitle,
                projectName
        ));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send task assignment email: " + e.getMessage());
        }
    }

    public void sendProjectInvitationEmail(String toEmail, String projectName, String inviterName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Project Invitation: " + projectName);
        message.setText(String.format(
                "Hello,\n\n" +
                "%s has added you to the project '%s'.\n\n" +
                "Please log in to the Task Management System to view project details and start collaborating.\n\n" +
                "Best regards,\n" +
                "Task Management Team",
                inviterName,
                projectName
        ));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send project invitation email: " + e.getMessage());
        }
    }
}
