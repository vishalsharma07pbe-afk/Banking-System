package com.vishal.bankingsystem.transaction.mapper;

import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.entity.Transaction;

public class TransactionMapper {

    public static TransactionDto entityToDto(Transaction transaction) {

        Long fromAccountId = transaction.getFromAccount() != null
                ? transaction.getFromAccount().getAccountId()
                : null;

        Long toAccountId = transaction.getToAccount() != null
                ? transaction.getToAccount().getAccountId()
                : null;

        return new TransactionDto(
                transaction.getTransactionId(),
                transaction.getReferenceNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                fromAccountId,
                toAccountId,
                transaction.getStatus(),
                transaction.getTransactionDate()
        );
    }
}