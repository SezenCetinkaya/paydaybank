package com.paydaybank.account_service.dto;

import com.paydaybank.account_service.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
    private UUID userId;
    private UUID accountId;
    private String email;
    private String accountNumber;
    private Account.AccountType accountType;
}
