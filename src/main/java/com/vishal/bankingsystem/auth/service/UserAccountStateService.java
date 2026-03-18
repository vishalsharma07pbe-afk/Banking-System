package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.repository.AccountRepository;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.customer.enums.CustomerStatus;
import com.vishal.bankingsystem.employee.entity.Employee;
import com.vishal.bankingsystem.employee.enums.EmployeeStatus;
import com.vishal.bankingsystem.exception.ResourceNotFoundException;
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
    private final AccountRepository accountRepository;

    @Transactional
    public UserEntity syncUserState(String username) {
        return userRepository.findByUserName(username)
                .map(this::evaluateUserState)
                .orElse(null);
    }

    @Transactional
    public void syncUserStateByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
        evaluateUserState(user);
    }

    @Transactional
    public void syncUserStateForCustomer(Long customerId) {
        UserEntity user = userRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not linked to customer id: " + customerId));
        evaluateUserState(user);
    }

    @Transactional
    public void syncUserStateForEmployee(Long employeeId) {
        UserEntity user = userRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not linked to employee id: " + employeeId));
        evaluateUserState(user);
    }

    private UserEntity evaluateUserState(UserEntity user) {
        if (user.getCustomer() != null) {
            return userRepository.save(evaluateCustomerState(user, user.getCustomer()));
        }

        if (user.getEmployee() != null) {
            return userRepository.save(evaluateEmployeeState(user, user.getEmployee()));
        }

        throw new IllegalStateException("User must be linked to exactly one identity");
    }

    private UserEntity evaluateCustomerState(UserEntity user, Customer customer) {
        boolean shouldDisable = customer.getStatus() == CustomerStatus.INACTIVE;
        boolean shouldLock = customer.getStatus() == CustomerStatus.BLOCKED;

        List<AccountEntity> accounts = accountRepository.findByCustomerCustomerId(customer.getCustomerId());
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

        applyUserState(user, shouldDisable, shouldLock);
        return user;
    }

    private UserEntity evaluateEmployeeState(UserEntity user, Employee employee) {
        boolean shouldDisable = false;
        boolean shouldLock = false;

        if (employee.getStatus() == EmployeeStatus.TERMINATED) {
            shouldDisable = true;
            shouldLock = true;
        }

        if (employee.getStatus() == EmployeeStatus.ON_LEAVE
                || employee.getStatus() == EmployeeStatus.SUSPENDED) {
            shouldLock = true;
        }

        applyUserState(user, shouldDisable, shouldLock);
        return user;
    }

    private void applyUserState(UserEntity user, boolean shouldDisable, boolean shouldLock) {
        user.setEnabled(!shouldDisable);
        user.setAccountLocked(shouldLock);
        if (shouldDisable) {
            user.setAccountExpiryDate(LocalDate.now());
        }
    }

    @Transactional
    @Scheduled(cron = "${app.security.auth-state-sync-cron:0 0 * * * *}")
    public void syncAllUsers() {
        userRepository.findAll()
                .forEach(this::evaluateUserState);
    }
}
