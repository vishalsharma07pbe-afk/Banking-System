package com.vishal.bankingsystem.transaction.dto;

import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    @NotNull(message = "Transaction ID is required")
    private Long transactionId;
    @NotBlank(message = "Reference number is required")
    private String referenceNumber;
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    private Long fromAccountId;
    private Long toAccountId;
    @NotNull(message = "Transaction status is required")
    private TransactionStatus status;
    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;

    public TransactionDto() {
    }

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

    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
    public void setToAccountId(Long toAccountId) { this.toAccountId = toAccountId; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
