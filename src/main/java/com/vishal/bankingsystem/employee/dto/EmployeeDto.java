package com.vishal.bankingsystem.employee.dto;

import com.vishal.bankingsystem.employee.enums.EmployeeRole;

public class EmployeeDto {

    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private EmployeeRole role;
    private String branchCode;

    public EmployeeDto(String employeeCode,
                       String firstName,
                       String lastName,
                       String email,
                       String phoneNumber,
                       EmployeeRole role,
                       String branchCode) {

        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.branchCode = branchCode;
    }

    public String getEmployeeCode() { return employeeCode; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public EmployeeRole getRole() { return role; }
    public String getBranchCode() { return branchCode; }
}