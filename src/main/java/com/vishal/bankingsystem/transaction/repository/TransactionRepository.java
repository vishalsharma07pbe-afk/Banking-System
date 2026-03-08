package com.vishal.bankingsystem.transaction.repository;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.transaction.entity.Transaction;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(AccountEntity fromAccount, AccountEntity toAccount);

    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    List<Transaction> findByTransactionType(TransactionType transactionType);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to);

    @Query("""
            select distinct t
            from Transaction t
            left join t.fromAccount fa
            left join t.toAccount ta
            where fa.accountNumber = :accountNumber
               or ta.accountNumber = :accountNumber
            """)
    List<Transaction> findByAccountNumber(String accountNumber);
}
