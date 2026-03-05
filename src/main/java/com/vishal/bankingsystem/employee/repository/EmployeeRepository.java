package com.vishal.bankingsystem.employee.repository;

import com.vishal.bankingsystem.employee.entity.Employee;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findTopByOrderByEmployeeIdDesc();
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findByEmail(String email);
    // Get employees by branch
    List<Employee> findByBranch_BranchCode(String branchCode);
    // Get employees by role
    List<Employee> findByRole(EmployeeRole role);
    // Get employees by branch and role
    List<Employee> findByBranch_BranchCodeAndRole(String branchCode, EmployeeRole role);

}
