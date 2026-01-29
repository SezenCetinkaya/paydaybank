package com.paydaybank.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
    private UUID userId;
    private String accountNumber;
}
