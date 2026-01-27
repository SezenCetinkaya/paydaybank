package com.paydaybank.email_confirmation_service.service;

import com.paydaybank.email_confirmation_service.entity.EmailConfirmation;
import com.paydaybank.email_confirmation_service.repository.EmailConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final EmailConfirmationRepository repository;

    public void confirmEmail(UUID id) {
        EmailConfirmation confirmation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Confirmation not found"));

        if (confirmation.getNotificationStatus() == EmailConfirmation.NotificationStatus.CONFIRMED) {
            return; 
        }

        confirmation.setNotificationStatus(EmailConfirmation.NotificationStatus.CONFIRMED);
        confirmation.setConfirmedAt(LocalDateTime.now());
        confirmation.setUpdatedAt(LocalDateTime.now());
        repository.save(confirmation);
    }
}
