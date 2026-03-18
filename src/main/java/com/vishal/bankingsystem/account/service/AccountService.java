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
    AccountDto updateAccount(String accountNumber,AccountDto accountDto);
    AccountDto getAccountByAccountNumber(String accountNumber);
    List<AccountDto> getAccountsByStatus(AccountStatus status);
    List<AccountDto> getAccountsByBalanceRange(BigDecimal min, BigDecimal max);
    List<AccountDto> getAccountsByCustomerId(Long customerId);
    AccountDto closeAccount(String accountNumber);
    AccountDto blockAccount(String accountNumber);
    AccountDto activateAccount(String accountNumber);
    AccountDto deposit(String accountNumber, BigDecimal amount);
    AccountDto withdraw(String accountNumber, BigDecimal amount);
    List<TransactionDto> getAccountTransactions(String accountNumber);
    void transferFunds(TransferRequest request);
}
