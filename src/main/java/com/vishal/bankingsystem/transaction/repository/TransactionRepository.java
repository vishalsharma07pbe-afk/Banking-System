package com.vishal.bankingsystem.transaction.repository;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountOrToAccount(AccountEntity fromAccount, AccountEntity toAccount);

}