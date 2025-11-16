package com.taskmanagement.config;

import com.taskmanagement.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class WebSocketAuthConfig {

    @Bean
    public SimpMessagingTemplate messagingTemplate(org.springframework.messaging.simp.SimpMessagingTemplate template,
                                                   NotificationService notificationService) {
        notificationService.setMessagingTemplate(template);
        return template;
    }
}
