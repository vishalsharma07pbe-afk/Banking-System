package com.vishal.bankingsystem.transaction.controller;

import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import com.vishal.bankingsystem.transaction.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/{transactionId}")
    public TransactionDto getById(@PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/reference/{referenceNumber}")
    public TransactionDto getByReferenceNumber(@PathVariable String referenceNumber) {
        return transactionService.getTransactionByReferenceNumber(referenceNumber);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping
    public List<TransactionDto> getTransactions(@RequestParam(required = false) String accountNumber,
                                                @RequestParam(required = false) TransactionType type,
                                                @RequestParam(required = false) TransactionStatus status,
                                                @RequestParam(required = false) LocalDateTime from,
                                                @RequestParam(required = false) LocalDateTime to) {
        if (accountNumber != null && !accountNumber.isBlank()) {
            return transactionService.getTransactionsByAccountNumber(accountNumber);
        }
        if (type != null) {
            return transactionService.getTransactionsByType(type);
        }
        if (status != null) {
            return transactionService.getTransactionsByStatus(status);
        }
        if (from != null && to != null) {
            return transactionService.getTransactionsByDateRange(from, to);
        }
        return transactionService.getAllTransactions();
    }
}
