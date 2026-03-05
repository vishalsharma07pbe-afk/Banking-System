package com.vishal.bankingsystem.transaction.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank(message = "Destination account number is required")
    private String toAccount;
    @NotBlank(message = "Source account number is required")
    private String fromAccount;
    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than 0")
    private BigDecimal transferAmount;

    public TransferRequest() {
    }

    public TransferRequest(String toAccount, String fromAccount, BigDecimal transferAmount) {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.transferAmount = transferAmount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }
}
