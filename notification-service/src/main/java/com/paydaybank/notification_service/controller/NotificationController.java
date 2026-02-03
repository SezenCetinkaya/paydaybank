package com.paydaybank.notification_service.controller;

import com.paydaybank.notification_service.dto.EmailConfirmationRequest;
import com.paydaybank.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email-confirmation")
    public ResponseEntity<String> sendEmailConfirmation(@RequestBody EmailConfirmationRequest request) {
        if ("ACCOUNT".equalsIgnoreCase(request.getType())) {
            notificationService.sendAccountConfirmation(
                request.getUserId(), 
                request.getAccountId(), 
                request.getEmail(),
                request.getAccountNumber(),
                request.getAccountType()
            );
        } else {
            notificationService.sendRegistrationConfirmation(request.getUserId(), request.getEmail());
        }
        return ResponseEntity.ok("Email confirmation triggered.");
    }
}
