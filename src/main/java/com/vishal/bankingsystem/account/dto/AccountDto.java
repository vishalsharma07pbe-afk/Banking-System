package com.vishal.bankingsystem.account.dto;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class AccountDto {
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    @NotNull(message = "Account type is required")
    private AccountType accountType;
    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance must be zero or greater")
    private BigDecimal balance;
    @NotNull(message = "Account status is required")
    private AccountStatus status;
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    @NotNull(message = "Branch ID is required")
    private Long branchId;

    public AccountDto() {}

    public AccountDto(String accountNumber, AccountType accountType, BigDecimal balance, AccountStatus status, Long customerId, Long branchId) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.customerId = customerId;
        this.branchId = branchId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }
}
