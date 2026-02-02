package com.paydaybank.transaction_service.kafka;

import com.paydaybank.transaction_service.dto.AccountOpenedEvent;
import com.paydaybank.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionListener {

    private final TransactionService transactionService;

    @KafkaListener(topics = "account-opened", groupId = "transaction-group-v2")
    public void handleAccountCreated(AccountOpenedEvent event) {
        log.info("Received account-opened event for accountId: {}", event.getAccountId());
        try {
            transactionService.createOpeningTransaction(event.getAccountId());
        } catch (Exception e) {
            log.error("Error processing account-opened event for accountId: {}", event.getAccountId(), e);
        }
    }
}
