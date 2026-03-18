package com.vishal.bankingsystem.account.repository;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountNumber(String accountNumber);
    List<AccountEntity> findByStatus(AccountStatus status);
    List<AccountEntity> findByBalanceBetween(BigDecimal min, BigDecimal max);
    List<AccountEntity> findByCustomerCustomerId(Long customerId);
}
