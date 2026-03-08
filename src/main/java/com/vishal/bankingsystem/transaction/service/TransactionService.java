package com.vishal.bankingsystem.transaction.service;

import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    TransactionDto getTransactionById(Long transactionId);
    TransactionDto getByReferenceNumber(String referenceNumber);
    List<TransactionDto> getAllTransactions();
    List<TransactionDto> getByAccountNumber(String accountNumber);
    List<TransactionDto> getByType(TransactionType type);
    List<TransactionDto> getByStatus(TransactionStatus status);
    List<TransactionDto> getByDateRange(LocalDateTime from, LocalDateTime to);
}
