package com.vishal.bankingsystem.transaction.entity;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(unique = true, nullable = false, updatable = false)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private AccountEntity fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private AccountEntity toAccount;

    protected Transaction() {}

    public Transaction(String referenceNumber,
                       TransactionType transactionType,
                       BigDecimal amount,
                       TransactionStatus status,
                       AccountEntity fromAccount,
                       AccountEntity toAccount) {

        this.referenceNumber = referenceNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionDate = LocalDateTime.now();
    }

    public Long getTransactionId() { return transactionId; }
    public String getReferenceNumber() { return referenceNumber; }
    public TransactionType getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public AccountEntity getFromAccount() { return fromAccount; }
    public AccountEntity getToAccount() { return toAccount; }
}