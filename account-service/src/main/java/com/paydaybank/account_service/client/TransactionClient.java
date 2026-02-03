package com.paydaybank.account_service.client;

import com.paydaybank.account_service.dto.TransactionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "transaction-service", url = "${APPLICATION_CONFIG_TRANSACTION_URL}", path = "/internal/transactions")
public interface TransactionClient {

    @GetMapping("/accounts/{accountId}")
    List<TransactionDTO> getTransactionsByAccountId(@PathVariable("accountId") UUID accountId);
}
