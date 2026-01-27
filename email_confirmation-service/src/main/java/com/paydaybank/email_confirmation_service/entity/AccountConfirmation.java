package com.paydaybank.email_confirmation_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@DiscriminatorValue("ACCOUNT_ACTIVATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountConfirmation extends EmailConfirmation {

    @Column(name = "account_id")
    private UUID accountId;
}
