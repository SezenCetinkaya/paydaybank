package com.paydaybank.notification_service.kafka;

import com.paydaybank.notification_service.dto.AccountOpenedEvent;
import com.paydaybank.notification_service.dto.UserCreatedEvent;
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
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user-created event: userId={}, email={}", event.getUserId(), event.getEmail());
        try {
            notificationService.sendRegistrationConfirmation(event.getUserId(), event.getEmail());
        } catch (Exception e) {
            log.error("Error processing user-created event for userId: {}", event.getUserId(), e);
        }
    }

    @KafkaListener(topics = "account-opened", groupId = "notification-group")
    public void handleAccountOpened(AccountOpenedEvent event) {
        log.info("Received account-opened event: userId={}, accountId={}, email={}", 
            event.getUserId(), event.getAccountId(), event.getEmail());
        try {
            notificationService.sendAccountConfirmation(
                event.getUserId(), 
                event.getAccountId(), 
                event.getEmail(),
                event.getAccountNumber(),
                event.getAccountType()
            );
        } catch (Exception e) {
            log.error("Error processing account-opened event for userId: {}, accountId: {}", 
                event.getUserId(), event.getAccountId(), e);
        }
    }
}
