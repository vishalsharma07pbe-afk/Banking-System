package com.vishal.bankingsystem.transaction.service;

import com.vishal.bankingsystem.exception.ResourceNotFoundException;
import com.vishal.bankingsystem.exception.BadRequestException;
import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.entity.Transaction;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import com.vishal.bankingsystem.transaction.mapper.TransactionMapper;
import com.vishal.bankingsystem.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionDto getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for id: " + transactionId));
        return TransactionMapper.entityToDto(transaction);
    }

    @Override
    public TransactionDto getTransactionByReferenceNumber(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for reference number: " + referenceNumber));
        return TransactionMapper.entityToDto(transaction);
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(TransactionMapper::entityToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByAccountNumber(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber).stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(TransactionMapper::entityToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByTransactionType(type).stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(TransactionMapper::entityToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(TransactionMapper::entityToDto)
                .toList();
    }

    @Override
    public List<TransactionDto> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' must be less than or equal to 'to'");
        }
        return transactionRepository.findByTransactionDateBetween(from, to).stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .map(TransactionMapper::entityToDto)
                .toList();
    }
}
