package com.paydaybank.account_service.dto;

import com.paydaybank.account_service.entity.Account;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateRequest {

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
}
