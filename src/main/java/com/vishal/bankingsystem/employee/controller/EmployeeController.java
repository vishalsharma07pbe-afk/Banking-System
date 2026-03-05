package com.vishal.bankingsystem.employee.controller;

import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;
import com.vishal.bankingsystem.employee.service.EmployeeService;
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
    @PostMapping
    public EmployeeDto createEmployee(@RequestBody EmployeeDto employeeDto) {
        return employeeService.createEmployee(employeeDto);
    }

    // Get Employee by employeeCode
    @GetMapping("/{employeeCode}")
    public EmployeeDto getEmployeeByCode(@PathVariable String employeeCode) {
        return employeeService.getEmployeeByCode(employeeCode);
    }

    // Get All Employees
    @GetMapping
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Update Employee
    @PutMapping("/{employeeCode}")
    public EmployeeDto updateEmployee(@PathVariable String employeeCode,
                                      @RequestBody EmployeeDto employeeDto) {
        return employeeService.updateEmployee(employeeCode, employeeDto);
    }

    // Delete Employee
    @DeleteMapping("/{employeeCode}")
    public void deleteEmployee(@PathVariable String employeeCode) {
        employeeService.deleteEmployee(employeeCode);
    }

    @GetMapping("/branch/{branchCode}")
    public List<EmployeeDto> getEmployeesByBranch(@PathVariable String branchCode){
        return employeeService.findByBranch_BranchCode(branchCode);
    }

    @GetMapping("/role/{role}")
    public List<EmployeeDto> getEmployeesByRole(@PathVariable EmployeeRole role){
        return employeeService.findByRole(role);
    }

    @GetMapping("/branch/{branchCode}/role/{role}")
    public List<EmployeeDto> getEmployeesByBranchAndRole(@PathVariable String branchCode,
                                                         @PathVariable EmployeeRole role){
        return employeeService.findByBranch_BranchCodeAndRole(branchCode, role);
    }

}