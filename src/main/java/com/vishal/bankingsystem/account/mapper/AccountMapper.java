package com.vishal.bankingsystem.account.mapper;
import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.entity.AccountEntity;

public class AccountMapper {
    public static AccountDto entityToDto(AccountEntity account) {
        return new AccountDto(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getStatus(),
                account.getCustomer().getCustomerId(),
                account.getBranch().getBranchId()
        );
    }

    // DTO → ENTITY
    // ⚠️ Notice: We DO NOT set Customer and Branch here
    // They must be set in Service layer after fetching from DB
    public static AccountEntity dtoToEntity(AccountDto accountDto) {
        AccountEntity account = new AccountEntity();
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setAccountType(accountDto.getAccountType());
        account.setBalance(accountDto.getBalance());
        account.setStatus(accountDto.getStatus());
        return account;
    }
}
