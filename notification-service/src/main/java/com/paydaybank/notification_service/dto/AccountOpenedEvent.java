package com.paydaybank.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpenedEvent {
    private UUID userId;
    private UUID accountId;
    private String email;
}
