package com.paydaybank.email_confirmation_service.controller;

import com.paydaybank.email_confirmation_service.service.ConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @GetMapping("/confirm")
    public String confirm(@RequestParam UUID id) {
        log.info("Received confirmation request for ID: {}", id);
        confirmationService.confirmEmail(id);
        log.info("Email confirmed successfully for ID: {}", id);
        return "Email Confirmed Successfully";
    }
}
