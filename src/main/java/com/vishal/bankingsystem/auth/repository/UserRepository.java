package com.vishal.bankingsystem.auth.repository;

import com.vishal.bankingsystem.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByCustomer_CustomerId(Long customerId);
    Optional<UserEntity> findByEmployee_EmployeeId(Long employeeId);
    boolean existsByCustomer_CustomerId(Long customerId);
    boolean existsByEmployee_EmployeeId(Long employeeId);
}
