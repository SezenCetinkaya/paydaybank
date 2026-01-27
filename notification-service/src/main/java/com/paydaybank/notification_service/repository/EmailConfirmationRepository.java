package com.paydaybank.notification_service.repository;

import com.paydaybank.notification_service.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, UUID> {
}
