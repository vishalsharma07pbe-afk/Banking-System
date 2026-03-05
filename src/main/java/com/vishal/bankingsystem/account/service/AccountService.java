package com.vishal.bankingsystem.account.service;

import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.request.TransferRequest;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    AccountDto getAccountById(Long id);
    AccountDto createAccount(AccountDto accountDto);
    void deleteAccount(String accountNumber);
    AccountDto updateAccount(String accountNumber,AccountDto accountDto);
    AccountDto getAccountByNumber (String accountNumber);
    List<AccountDto> findByStatus(AccountStatus status);
    List<AccountDto> findByBalanceBetween(BigDecimal min, BigDecimal max);
    List<AccountDto> findByCustomerEmail(String email);
    AccountDto closeAccount(String accountNumber);
    AccountDto blockAccount(String accountNumber);
    AccountDto activateAccount(String accountNumber);
    AccountDto deposit(String accountNumber, BigDecimal amount);
    AccountDto withdraw(String accountNumber, BigDecimal amount);
    List<TransactionDto> transactionHistory(String accountNumber);
    void transfer(TransferRequest request);
}
