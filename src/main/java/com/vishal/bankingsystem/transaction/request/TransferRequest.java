package com.vishal.bankingsystem.transaction.request;

import java.math.BigDecimal;

public class TransferRequest {
    private String toAccount;
    private String fromAccount;
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
}