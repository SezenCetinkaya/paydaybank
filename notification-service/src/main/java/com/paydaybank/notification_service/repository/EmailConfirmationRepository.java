package com.paydaybank.notification_service.repository;

import com.paydaybank.notification_service.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, UUID> {
    java.util.Optional<EmailConfirmation> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @org.springframework.data.jpa.repository.Query("SELECT e FROM EmailConfirmation e WHERE TYPE(e) = AccountConfirmation AND e.accountId = :accountId ORDER BY e.createdAt DESC")
    java.util.List<EmailConfirmation> findLatestByAccountId(@org.springframework.data.repository.query.Param("accountId") UUID accountId);
}
