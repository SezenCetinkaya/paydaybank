package com.paydaybank.transaction_service.service;

import com.paydaybank.transaction_service.dto.TransactionDTO;
import com.paydaybank.transaction_service.entity.Transaction;
import com.paydaybank.transaction_service.entity.TransactionType;
import com.paydaybank.transaction_service.mapper.TransactionMapper;
import com.paydaybank.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public void createOpeningTransaction(UUID accountId) {
        log.info("Creating opening transaction for accountId: {}", accountId);

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(BigDecimal.ZERO)
                .description("Account Opening")
                .type(TransactionType.OPENING)
                .build();

        transactionRepository.save(transaction);
        log.info("Opening transaction created successfully for accountId: {}", accountId);
    }

    public List<TransactionDTO> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findAllByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(transactionMapper::toDTO)
                .toList();
    }
}
