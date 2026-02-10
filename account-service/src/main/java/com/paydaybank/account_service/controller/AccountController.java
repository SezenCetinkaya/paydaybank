package com.paydaybank.account_service.controller;

import com.paydaybank.account_service.dto.AccountCreateRequest;
import com.paydaybank.account_service.dto.AccountDTO;
import com.paydaybank.account_service.dto.TransactionDTO;
import com.paydaybank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                                                    @RequestHeader("X-User-Id") UUID userId,
                                                    @RequestHeader("X-User-Email") String email) {
        return new ResponseEntity<>(accountService.createAccount(userId, request, email), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@PathVariable UUID accountId,
                                                                @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(accountService.getTransactionsByAccountId(accountId, userId));
    }
}
