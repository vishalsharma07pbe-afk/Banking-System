package com.vishal.bankingsystem.transaction.service;

import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionDto getTransactionById(Long transactionId);
    TransactionDto getTransactionByReferenceNumber(String referenceNumber);
    List<TransactionDto> getAllTransactions();
    List<TransactionDto> getTransactionsByAccountNumber(String accountNumber);
    List<TransactionDto> getTransactionsByType(TransactionType type);
    List<TransactionDto> getTransactionsByStatus(TransactionStatus status);
    List<TransactionDto> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to);
}
