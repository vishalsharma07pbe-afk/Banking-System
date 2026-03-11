package com.vishal.bankingsystem.employee.service;

import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);
    EmployeeDto getEmployeeByEmployeeCode(String employeeCode);
    List<EmployeeDto> getAllEmployees();
    EmployeeDto updateEmployee(String employeeCode, EmployeeDto employeeDto);
    void terminateEmployee(String employeeCode);
    List<EmployeeDto> getEmployeesByBranchCode(String branchCode);
    List<EmployeeDto> getEmployeesByRole(EmployeeRole role);
    List<EmployeeDto> getEmployeesByBranchCodeAndRole(String branchCode, EmployeeRole role);
}
