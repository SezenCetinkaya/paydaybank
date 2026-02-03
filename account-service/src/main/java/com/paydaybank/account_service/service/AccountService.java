package com.paydaybank.account_service.service;

import com.paydaybank.account_service.client.TransactionClient;
import com.paydaybank.account_service.dto.AccountCreateRequest;
import com.paydaybank.account_service.dto.AccountCreatedEvent;
import com.paydaybank.account_service.dto.AccountDTO;
import com.paydaybank.account_service.dto.TransactionDTO;
import com.paydaybank.account_service.entity.Account;
import com.paydaybank.account_service.mapper.AccountMapper;
import com.paydaybank.account_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransactionClient transactionClient;


    @Transactional
    public AccountDTO createAccount(UUID userId, AccountCreateRequest request, String email) {
        log.info("Creating account for userId: {}", userId);

        String accountNumber = UUID.randomUUID().toString();

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(Account.AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);

        publishAccountCreatedEvent(account, email);

        return accountMapper.toDTO(account);
    }

    public List<AccountDTO> getAccountsByUserId(UUID userId) {
        return accountRepository.findAllByUserId(userId).stream()
                .map(accountMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByAccountId(UUID accountId) {
        return transactionClient.getTransactionsByAccountId(accountId);
    }

    public boolean validateAccountOwnership(UUID accountId, UUID userId) {
        return accountRepository.findById(accountId)
                .map(account -> account.getUserId().equals(userId))
                .orElse(false);
    }

    private void publishAccountCreatedEvent(Account account, String email) {
        AccountCreatedEvent event = new AccountCreatedEvent(account.getUserId(), account.getId(), email, account.getAccountNumber());
        log.info("PUBLISHING EVENT: AccountCreatedEvent for accountId: {}, accountNumber: {}", account.getId(), account.getAccountNumber());
        kafkaTemplate.send("account-opened", event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("AccountCreatedEvent sent to Kafka successfully for accountId: {}", account.getId());
            } else {
                log.error("Failed to send AccountCreatedEvent to Kafka for accountId: {}", account.getId(), ex);
            }
        });
    }

}
