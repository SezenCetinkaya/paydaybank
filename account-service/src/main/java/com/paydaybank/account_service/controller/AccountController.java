package com.paydaybank.account_service.controller;

import com.paydaybank.account_service.dto.AccountCreateRequest;
import com.paydaybank.account_service.dto.AccountDTO;
import com.paydaybank.account_service.dto.TransactionDTO;
import com.paydaybank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody @Valid AccountCreateRequest request,
                                                    @AuthenticationPrincipal com.paydaybank.account_service.security.CustomPrincipal principal) {
        return new ResponseEntity<>(accountService.createAccount(principal.getId(), request, principal.getEmail()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@PathVariable UUID accountId) {
        return ResponseEntity.ok(accountService.getTransactionsByAccountId(accountId));
    }
}
