package com.vishal.bankingsystem.employee.controller;

import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;
import com.vishal.bankingsystem.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create Employee
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN')")
    @PostMapping
    public EmployeeDto createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        return employeeService.createEmployee(employeeDto);
    }

    // Get Employee by employeeCode
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/{employeeCode}")
    public EmployeeDto getEmployeeByEmployeeCode(@PathVariable String employeeCode) {
        return employeeService.getEmployeeByEmployeeCode(employeeCode);
    }

    // Get All Employees
    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Update Employee
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{employeeCode}")
    public EmployeeDto updateEmployee(@PathVariable String employeeCode,
                                      @Valid @RequestBody EmployeeDto employeeDto) {
        return employeeService.updateEmployee(employeeCode, employeeDto);
    }

    // Delete Employee
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{employeeCode}")
    public void terminateEmployee(@PathVariable String employeeCode) {
        employeeService.terminateEmployee(employeeCode);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/branch/{branchCode}")
    public List<EmployeeDto> getEmployeesByBranch(@PathVariable String branchCode){
        return employeeService.getEmployeesByBranchCode(branchCode);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/role/{role}")
    public List<EmployeeDto> getEmployeesByRole(@PathVariable EmployeeRole role){
        return employeeService.getEmployeesByRole(role);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/branch/{branchCode}/role/{role}")
    public List<EmployeeDto> getEmployeesByBranchAndRole(@PathVariable String branchCode,
                                                         @PathVariable EmployeeRole role){
        return employeeService.getEmployeesByBranchCodeAndRole(branchCode, role);
    }

}
