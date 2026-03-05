package com.vishal.bankingsystem.branch.entity;

import com.vishal.bankingsystem.branch.enums.BranchStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "branches")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    @Column(nullable = false, unique = true)
    private String branchCode;

    @Column(nullable = false)
    private String branchName;

    @Column(nullable = false, unique = true)
    private String ifscCode;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    private BranchStatus status;

    private LocalDateTime createdAt;

    public Branch() {
        this.createdAt = LocalDateTime.now();
        this.status = BranchStatus.ACTIVE;
    }

    public Branch(Long branchId, String branchCode, String branchName, String ifscCode, String phoneNumber, String email, String addressLine1, String addressLine2, String city, String state, String country, String postalCode, BranchStatus status, LocalDateTime createdAt) {
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.ifscCode = ifscCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getBranchId() { return branchId; }

    public String getBranchCode() { return branchCode; }

    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }

    public String getBranchName() { return branchName; }

    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getIfscCode() { return ifscCode; }

    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getAddressLine1() { return addressLine1; }

    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }

    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public BranchStatus getStatus() { return status; }

    public void setStatus(BranchStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
