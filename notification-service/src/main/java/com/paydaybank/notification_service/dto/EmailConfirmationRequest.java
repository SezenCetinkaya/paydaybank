package com.paydaybank.notification_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EmailConfirmationRequest {
    private UUID userId;
    private UUID accountId; 
    private String email;
    private String type; 
}
