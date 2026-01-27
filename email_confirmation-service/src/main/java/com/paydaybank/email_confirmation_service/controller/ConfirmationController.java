package com.paydaybank.email_confirmation_service.controller;

import com.paydaybank.email_confirmation_service.service.ConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @GetMapping("/confirm")
    public String confirm(@RequestParam UUID id) {
        confirmationService.confirmEmail(id);
        return "Email Confirmed Successfully";
    }
}
