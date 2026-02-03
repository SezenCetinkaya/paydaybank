package com.paydaybank.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TransactionType {OPENING,DEBIT,CREDIT}
}
