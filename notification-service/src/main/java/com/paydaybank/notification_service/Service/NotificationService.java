package com.paydaybank.notification_service.service;

import com.paydaybank.notification_service.client.UserClient;
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
    private final UserClient userClient;

    @Value("${email-confirmation.base-url:http://localhost:8088/confirm}")
    private String confirmationBaseUrl;

    public void sendRegistrationConfirmation(UUID userId, String email) {
        // Check if there is an existing confirmation for this user
        boolean exists = emailConfirmationRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(this::isValid)
                .orElse(false);

        if (exists) {
            log.info("A valid registration confirmation already exists for userId: {}. Skipping email.", userId);
            return;
        }

        RegistrationConfirmation confirmation = RegistrationConfirmation.builder()
                .userId(userId)
                .notificationStatus(EmailConfirmation.NotificationStatus.SENT)
                .build();
        
        saveAndSend(confirmation, email, "Welcome to Payday Bank - Confirm Registration", userId, "registration");
    }

    public void sendAccountConfirmation(UUID userId, UUID accountId, String email, String accountNumber, String accountType) {
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

        saveAndSend(confirmation, email, "Confirm Your New Account", userId, "account opening", accountNumber, accountType);
    }

    private boolean isValid(EmailConfirmation confirmation) {
        return confirmation.getNotificationStatus() == EmailConfirmation.NotificationStatus.SENT ||
               confirmation.getNotificationStatus() == EmailConfirmation.NotificationStatus.CONFIRMED;
    }

    private void saveAndSend(EmailConfirmation confirmation, String email, String subject, UUID userId, String detailsType) {
        saveAndSend(confirmation, email, subject, userId, detailsType, null, null);
    }

    private void saveAndSend(EmailConfirmation confirmation, String email, String subject, UUID userId, String detailsType, String accountNumber, String accountType) {
        EmailConfirmation saved = emailConfirmationRepository.save(confirmation);
        String link = confirmationBaseUrl + "?id=" + saved.getId();
        
        StringBuilder body = new StringBuilder();
        try {
            UserClient.UserResponse user = userClient.getUserById(userId);
            if (user != null) {
                body.append(String.format("Dear %s %s,\n\n", user.getFirstName(), user.getLastName()));
                body.append(String.format("Your %s details are as follows:\n", detailsType));
                body.append(String.format("- Full Name: %s %s\n", user.getFirstName(), user.getLastName()));
                body.append(String.format("- Gender: %s\n", user.getGender()));
                body.append(String.format("- Date of Birth: %s\n", user.getDateOfBirth()));
                body.append(String.format("- Phone Number: %s\n", user.getPhoneNumber()));
                body.append(String.format("- Email: %s\n", user.getEmail()));
            } else {
                body.append("Dear Customer,\n\n");
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user details for userId: {}. Proceeding with generic email.", userId, e);
            body.append("Dear Customer,\n\n");
        }

        if (accountNumber != null && accountType != null) {
            body.append("\nAccount Details:\n");
            body.append(String.format("- Account Number: %s\n", accountNumber));
            body.append(String.format("- Account Type: %s\n", accountType));
        }

        body.append("\nPlease click the link to confirm: ").append(link);
        body.append("\n\nBest regards,\nPayday Bank Team");
        
        try {
            emailSender.sendEmail(email, subject, body.toString());
            log.info("Notification sent successfully for confirmation ID: {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to send email for confirmation ID: {}. Marking as FAILED.", saved.getId());
            saved.setNotificationStatus(EmailConfirmation.NotificationStatus.FAILED);
            emailConfirmationRepository.save(saved);
        }
    }
}
