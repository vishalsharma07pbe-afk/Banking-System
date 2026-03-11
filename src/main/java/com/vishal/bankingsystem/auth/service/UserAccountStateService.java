package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.repository.AccountRepository;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.customer.enums.CustomerStatus;
import com.vishal.bankingsystem.customer.repository.CustomerRepository;
import com.vishal.bankingsystem.employee.entity.Employee;
import com.vishal.bankingsystem.employee.enums.EmployeeStatus;
import com.vishal.bankingsystem.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountStateService {
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public UserEntity syncUserState(String username) {
        UserEntity user = userRepository.findByUserName(username).orElse(null);
        if (user == null) {
            return null;
        }

        boolean shouldDisable = false;
        boolean shouldLock = false;
        LocalDate today = LocalDate.now();

        Customer customer = resolveCustomer(user, username);
        if (customer != null) {
            if (customer.getStatus() == CustomerStatus.INACTIVE) {
                shouldDisable = true;
            }
            if (customer.getStatus() == CustomerStatus.BLOCKED) {
                shouldLock = true;
            }

            List<AccountEntity> accounts = accountRepository.findByCustomerEmail(username);
            if (!accounts.isEmpty()) {
                boolean hasActiveAccount = accounts.stream()
                        .anyMatch(account -> account.getStatus() == AccountStatus.ACTIVE);
                boolean hasBlockedAccount = accounts.stream()
                        .anyMatch(account -> account.getStatus() == AccountStatus.BLOCKED);
                boolean allClosed = accounts.stream()
                        .allMatch(account -> account.getStatus() == AccountStatus.CLOSED);

                if (!hasActiveAccount && hasBlockedAccount) {
                    shouldLock = true;
                }
                if (allClosed) {
                    shouldDisable = true;
                }
            }
        }

        Employee employee = resolveEmployee(user, username);
        if (employee != null) {
            if (employee.getStatus() == EmployeeStatus.TERMINATED) {
                shouldDisable = true;
                shouldLock = true;
            }
            if (employee.getStatus() == EmployeeStatus.ON_LEAVE
                    || employee.getStatus() == EmployeeStatus.SUSPENDED) {
                shouldLock = true;
            }
        }

        user.setEnabled(!shouldDisable);
        user.setAccountLocked(shouldLock);
        if (shouldDisable) {
            user.setAccountExpiryDate(today);
        }

        return userRepository.save(user);
    }

    private Customer resolveCustomer(UserEntity user, String username) {
        if (user.getCustomer() != null) {
            return user.getCustomer();
        }
        return customerRepository.findByEmail(username).orElse(null);
    }

    private Employee resolveEmployee(UserEntity user, String username) {
        if (user.getEmployee() != null) {
            return user.getEmployee();
        }
        return employeeRepository.findByEmail(username).orElse(null);
    }

    @Transactional
    @Scheduled(cron = "${app.security.auth-state-sync-cron:0 0 * * * *}")
    public void syncAllUsers() {
        userRepository.findAll()
                .forEach(user -> syncUserState(user.getUserName()));
    }
}
