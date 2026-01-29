package com.paydaybank.account_service.mapper;

import com.paydaybank.account_service.dto.AccountDTO;
import com.paydaybank.account_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toDTO(Account account);
}
