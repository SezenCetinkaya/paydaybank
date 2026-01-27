package com.paydaybank.notification_service.kafka;

import com.paydaybank.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;
    
    @KafkaListener(topics = "user-created", groupId = "notification-group")
    public void handleUserCreated(String message) {
        log.info("Received user-created event: {}", message);
        // Protocol: userId,email
        // Simple parsing for demonstration. Realworld would use a DTO.
        try {
            String[] parts = message.split(",");
            if (parts.length >= 2) {
                java.util.UUID userId = java.util.UUID.fromString(parts[0].trim());
                String email = parts[1].trim();
                notificationService.sendRegistrationConfirmation(userId, email);
            }
        } catch (Exception e) {
            log.error("Error processing user-created event", e);
        }
    }

    @KafkaListener(topics = "account-opened", groupId = "notification-group")
    public void handleAccountOpened(String message) {
        log.info("Received account-opened event: {}", message);
        // Protocol: userId,accountId,email
        try {
            String[] parts = message.split(",");
            if (parts.length >= 3) {
                java.util.UUID userId = java.util.UUID.fromString(parts[0].trim());
                java.util.UUID accountId = java.util.UUID.fromString(parts[1].trim());
                String email = parts[2].trim();
                notificationService.sendAccountConfirmation(userId, accountId, email);
            }
        } catch (Exception e) {
            log.error("Error processing account-opened event", e);
        }
    }
}
