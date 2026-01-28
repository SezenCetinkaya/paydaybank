package com.paydaybank.notification_service.service;

import com.paydaybank.notification_service.entity.AccountConfirmation;
import com.paydaybank.notification_service.entity.EmailConfirmation;
import com.paydaybank.notification_service.entity.RegistrationConfirmation;
import com.paydaybank.notification_service.repository.EmailConfirmationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final EmailSender emailSender;

    @Value("${email-confirmation.base-url:http://localhost:8088/confirm}")
    private String confirmationBaseUrl;

    public void sendRegistrationConfirmation(UUID userId, String email) {
        // Check if there is an existing confirmation for this user
        emailConfirmationRepository.findTopByUserIdOrderByCreatedAtDesc(userId).ifPresent(existing -> {
            if (isValid(existing)) {
                log.info("A valid registration confirmation already exists for userId: {}. Skipping email.", userId);
                return;
            }
        });

        boolean alreadyExists = emailConfirmationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(this::isValid)
                .orElse(false);

        if (alreadyExists) {
             log.info("Skipping registration email for userId: {} (Already sent/confirmed)", userId);
             return;
        }

        RegistrationConfirmation confirmation = RegistrationConfirmation.builder()
                .userId(userId)
                .notificationStatus(EmailConfirmation.NotificationStatus.SENT)
                .build();
        
        saveAndSend(confirmation, email, "Welcome to Payday Bank - Confirm Registration");
    }

    public void sendAccountConfirmation(UUID userId, UUID accountId, String email) {
        // Check if there is an existing confirmation for this account
        boolean alreadyExists = emailConfirmationRepository.findLatestByAccountId(accountId).stream()
                .findFirst()
                .map(this::isValid)
                .orElse(false);

        if (alreadyExists) {
            log.info("Skipping account email for accountId: {} (Already sent/confirmed)", accountId);
            return;
        }

        AccountConfirmation confirmation = AccountConfirmation.builder()
                .userId(userId)
                .accountId(accountId)
                .notificationStatus(EmailConfirmation.NotificationStatus.SENT)
                .build();

        saveAndSend(confirmation, email, "Confirm Your New Account");
    }

    private boolean isValid(EmailConfirmation confirmation) {
        return confirmation.getNotificationStatus() == EmailConfirmation.NotificationStatus.SENT ||
               confirmation.getNotificationStatus() == EmailConfirmation.NotificationStatus.CONFIRMED;
    }

    private void saveAndSend(EmailConfirmation confirmation, String email, String subject) {
        EmailConfirmation saved = emailConfirmationRepository.save(confirmation);
        String link = confirmationBaseUrl + "?id=" + saved.getId();
        
        String body = "Please click the link to confirm: " + link;
        
        try {
            emailSender.sendEmail(email, subject, body);
            log.info("Notification sent successfully for confirmation ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to send email for confirmation ID: {}. Marking as FAILED.", saved.getId());
            saved.setNotificationStatus(EmailConfirmation.NotificationStatus.FAILED);
            emailConfirmationRepository.save(saved);
        }
    }
}
