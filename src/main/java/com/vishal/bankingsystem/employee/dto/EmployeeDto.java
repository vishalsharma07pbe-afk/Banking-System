package com.vishal.bankingsystem.employee.dto;

import com.vishal.bankingsystem.employee.enums.EmployeeRole;
import com.vishal.bankingsystem.employee.enums.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeDto {
    
    private String employeeCode;
    @NotBlank(message = "First name is required")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotNull(message = "Employee role is required")
    private EmployeeRole role;
    private EmployeeStatus status;
    @NotBlank(message = "Branch code is required")
    private String branchCode;

    public EmployeeDto() {
    }

    public EmployeeDto(String employeeCode,
                       String firstName,
                       String lastName,
                       String email,
                       String phoneNumber,
                       EmployeeRole role,
                       EmployeeStatus status,
                       String branchCode) {

        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
        this.branchCode = branchCode;
    }

    public String getEmployeeCode() { return employeeCode; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public EmployeeRole getRole() { return role; }
    public EmployeeStatus getStatus() { return status; }
    public String getBranchCode() { return branchCode; }

    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setRole(EmployeeRole role) { this.role = role; }
    public void setStatus(EmployeeStatus status) { this.status = status; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
}
