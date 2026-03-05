package com.vishal.bankingsystem.branch.dto;

import com.vishal.bankingsystem.branch.enums.BranchStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BranchDto {
        @NotBlank(message = "Branch code is required")
        private String branchCode;
        @NotBlank(message = "Branch name is required")
        private String branchName;
        @NotBlank(message = "IFSC code is required")
        private String ifscCode;
        @NotBlank(message = "Phone number is required")
        private String phoneNumber;
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        private String email;
        @NotBlank(message = "City is required")
        private String city;
        @NotBlank(message = "State is required")
        private String state;
        @NotBlank(message = "Country is required")
        private String country;
        @NotBlank(message = "Postal code is required")
        private String postalCode;
        @NotNull(message = "Branch status is required")
        private BranchStatus status;

    public BranchDto() {
    }

    public BranchDto(String branchCode, String branchName, String ifscCode, String phoneNumber, String email, String city, String state, String country, String postalCode, BranchStatus status) {
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.ifscCode = ifscCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.status = status;
    }

    public BranchStatus getStatus() {
        return status;
    }

    public void setStatus(BranchStatus status) {
        this.status = status;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
