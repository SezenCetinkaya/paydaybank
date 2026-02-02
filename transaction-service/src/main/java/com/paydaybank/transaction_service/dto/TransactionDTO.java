package com.paydaybank.transaction_service.dto;

import com.paydaybank.transaction_service.entity.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransactionDTO {
    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
