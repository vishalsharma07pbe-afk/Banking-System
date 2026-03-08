package com.vishal.bankingsystem.auth.repository;

import com.vishal.bankingsystem.auth.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByUserName(String userName);
}
