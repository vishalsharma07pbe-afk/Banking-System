package com.vishal.bankingsystem.transaction.dto;

import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    private Long transactionId;
    private String referenceNumber;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long fromAccountId;
    private Long toAccountId;
    private TransactionStatus status;
    private LocalDateTime transactionDate;

    public TransactionDto(Long transactionId,
                          String referenceNumber,
                          TransactionType transactionType,
                          BigDecimal amount,
                          Long fromAccountId,
                          Long toAccountId,
                          TransactionStatus status,
                          LocalDateTime transactionDate) {

        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    public Long getTransactionId() { return transactionId; }
    public String getReferenceNumber() { return referenceNumber; }
    public TransactionType getTransactionType() { return transactionType; }
    public BigDecimal getAmount() { return amount; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
}