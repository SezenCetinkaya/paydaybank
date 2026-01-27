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
        RegistrationConfirmation confirmation = RegistrationConfirmation.builder()
                .userId(userId)
                .notificationStatus(EmailConfirmation.NotificationStatus.SENT)
                .build();
        
        saveAndSend(confirmation, email, "Welcome to Payday Bank - Confirm Registration");
    }

    public void sendAccountConfirmation(UUID userId, UUID accountId, String email) {
        AccountConfirmation confirmation = AccountConfirmation.builder()
                .userId(userId)
                .accountId(accountId)
                .notificationStatus(EmailConfirmation.NotificationStatus.SENT)
                .build();

        saveAndSend(confirmation, email, "Confirm Your New Account");
    }

    private void saveAndSend(EmailConfirmation confirmation, String email, String subject) {
        EmailConfirmation saved = emailConfirmationRepository.save(confirmation);
        String link = confirmationBaseUrl + "?id=" + saved.getId();
        
        String body = "Please click the link to confirm: " + link;
        
        emailSender.sendEmail(email, subject, body);
        
        log.info("Notification process completed for confirmation ID: {}", saved.getId());
    }
}
