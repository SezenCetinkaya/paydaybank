package com.paydaybank.auth.repository;

import com.paydaybank.auth.entity.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<Authorization, UUID> {
    Optional<Authorization> findByEmail(String email);
}
