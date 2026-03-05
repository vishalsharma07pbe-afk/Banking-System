package com.vishal.bankingsystem.employee.service;

import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);
    EmployeeDto getEmployeeByCode(String employeeCode);
    List<EmployeeDto> getAllEmployees();
    EmployeeDto updateEmployee(String employeeCode, EmployeeDto employeeDto);
    void deleteEmployee(String employeeCode);
    // Get employees by branch
    List<EmployeeDto> findByBranch_BranchCode(String branchCode);
    // Get employees by role
    List<EmployeeDto> findByRole(EmployeeRole role);
    // Get employees by branch and role
    List<EmployeeDto> findByBranch_BranchCodeAndRole(String branchCode, EmployeeRole role);
}