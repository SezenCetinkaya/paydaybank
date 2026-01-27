package com.paydaybank.notification_service.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("REGISTRATION")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegistrationConfirmation extends EmailConfirmation {
}
