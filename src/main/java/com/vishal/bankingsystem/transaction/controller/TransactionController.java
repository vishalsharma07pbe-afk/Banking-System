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
    @GetMapping("/id/{transactionId}")
    public TransactionDto getById(@PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/reference/{referenceNumber}")
    public TransactionDto getByReferenceNumber(@PathVariable String referenceNumber) {
        return transactionService.getByReferenceNumber(referenceNumber);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/account/{accountNumber}")
    public List<TransactionDto> getByAccountNumber(@PathVariable String accountNumber) {
        return transactionService.getByAccountNumber(accountNumber);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/type/{type}")
    public List<TransactionDto> getByType(@PathVariable TransactionType type) {
        return transactionService.getByType(type);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/status/{status}")
    public List<TransactionDto> getByStatus(@PathVariable TransactionStatus status) {
        return transactionService.getByStatus(status);
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/date-range")
    public List<TransactionDto> getByDateRange(@RequestParam LocalDateTime from,
                                               @RequestParam LocalDateTime to) {
        return transactionService.getByDateRange(from, to);
    }
}
