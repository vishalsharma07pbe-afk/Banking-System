package com.vishal.bankingsystem.auth.entity;

import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountLocked = false;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private LocalDateTime lockUntil;

    @Column(nullable = false)
    private LocalDate accountExpiryDate;

    @Column(nullable = false)
    private LocalDate passwordChangedAt;

    @Column(nullable = false)
    private LocalDate passwordExpiryDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")

    )
    private Set<RoleEntity> roles = new HashSet<>();

    @PrePersist
    @PreUpdate
    void applyDefaults() {
        validateLinkedIdentity();

        if (accountExpiryDate == null) {
            accountExpiryDate = LocalDate.now().plusYears(1);
        }
        if (passwordChangedAt == null) {
            passwordChangedAt = LocalDate.now();
        }
        if (passwordExpiryDate == null) {
            passwordExpiryDate = passwordChangedAt.plusMonths(6);
        }
    }

    private void validateLinkedIdentity() {
        boolean hasCustomer = customer != null;
        boolean hasEmployee = employee != null;

        if (hasCustomer == hasEmployee) {
            throw new IllegalStateException("User must be linked to exactly one identity: customer or employee");
        }
    }
}
