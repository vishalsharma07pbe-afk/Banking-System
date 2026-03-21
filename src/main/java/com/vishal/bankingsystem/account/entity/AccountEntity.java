package com.vishal.bankingsystem.account.entity;

import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.enums.AccountType;
import com.vishal.bankingsystem.branch.entity.Branch;
import com.vishal.bankingsystem.customer.entity.Customer;
import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
@Table(
        name = "account_entity",
        uniqueConstraints = @UniqueConstraint(name = "uk_account_customer_type", columnNames = {"customer_id", "account_type"})
)
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    public AccountEntity() {
    }

    public AccountEntity(String accountNumber, AccountType accountType, BigDecimal balance, AccountStatus status, Customer customer, Branch branch) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.customer = customer;
        this.branch = branch;
    }

    public Long getAccountId() {
        return accountId;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
