package com.paydaybank.email_confirmation_service.repository;

import com.paydaybank.email_confirmation_service.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, UUID> {
}
