package com.paydaybank.transaction_service.repository;

import com.paydaybank.transaction_service.entity.Transaction;
import com.paydaybank.transaction_service.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByAccountIdOrderByCreatedAtDesc(UUID accountId);

    boolean existsByAccountIdAndType(UUID accountId, TransactionType type);
}
