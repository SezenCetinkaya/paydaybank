package com.paydaybank.account_service.controller;

import com.paydaybank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<Boolean> validateAccountOwnership(@PathVariable UUID accountId, 
                                                            @RequestParam UUID userId) {
        return ResponseEntity.ok(accountService.validateAccountOwnership(accountId, userId));
    }
}
