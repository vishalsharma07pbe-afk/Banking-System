package com.vishal.bankingsystem.employee.service;

import com.vishal.bankingsystem.branch.entity.Branch;
import com.vishal.bankingsystem.branch.repository.BranchRepository;
import com.vishal.bankingsystem.auth.service.UserAccountStateService;
import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.entity.Employee;
import com.vishal.bankingsystem.employee.enums.EmployeeRole;
import com.vishal.bankingsystem.employee.enums.EmployeeStatus;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.ResourceNotFoundException;
import com.vishal.bankingsystem.employee.mapper.EmployeeMapper;
import com.vishal.bankingsystem.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final UserAccountStateService userAccountStateService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               BranchRepository branchRepository,
                               UserAccountStateService userAccountStateService) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.userAccountStateService = userAccountStateService;
    }

    // Employee Code Generator
    private String generateEmployeeCode() {

        Employee lastEmployee = employeeRepository
                .findTopByOrderByEmployeeIdDesc()
                .orElse(null);

        if (lastEmployee == null) {
            return "EMP-0001";
        }

        String lastCode = lastEmployee.getEmployeeCode();
        int number = Integer.parseInt(lastCode.substring(4));
        number++;

        return String.format("EMP-%04d", number);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        Branch branch = branchRepository.findByBranchCode(employeeDto.getBranchCode())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        Optional<Employee> existingEmployee = employeeRepository.findByEmail(employeeDto.getEmail());
        Employee employee;

        if (existingEmployee.isPresent()) {
            employee = existingEmployee.get();
            if (employee.getStatus() != EmployeeStatus.TERMINATED) {
                throw new ConflictException("Employee already exists and is not terminated");
            }
        } else {
            employee = EmployeeMapper.dtoToEntity(employeeDto);
            employee.setEmployeeCode(generateEmployeeCode());
        }

        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setRole(employeeDto.getRole());
        employee.setStatus(employeeDto.getStatus() == null ? EmployeeStatus.ACTIVE : employeeDto.getStatus());
        employee.setBranch(branch);

        Employee savedEmployee = employeeRepository.save(employee);
        userAccountStateService.syncUserState(savedEmployee.getEmail());

        return EmployeeMapper.entityToDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeByCode(String employeeCode) {

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        return EmployeeMapper.entityToDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE)
                .map(EmployeeMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto updateEmployee(String employeeCode, EmployeeDto employeeDto) {

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Branch branch = branchRepository.findByBranchCode(employeeDto.getBranchCode())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        String previousEmail = employee.getEmail();
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setRole(employeeDto.getRole());
        employee.setStatus(employeeDto.getStatus() == null ? EmployeeStatus.ACTIVE : employeeDto.getStatus());
        employee.setBranch(branch);

        Employee savedEmployee = employeeRepository.save(employee);
        userAccountStateService.syncUserState(previousEmail);
        userAccountStateService.syncUserState(savedEmployee.getEmail());

        return EmployeeMapper.entityToDto(savedEmployee);
    }

    @Override
    public void deleteEmployee(String employeeCode) {

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        employee.setStatus(EmployeeStatus.TERMINATED);
        Employee savedEmployee = employeeRepository.save(employee);
        userAccountStateService.syncUserState(savedEmployee.getEmail());
    }

    @Override
    public List<EmployeeDto> findByBranch_BranchCode(String branchCode) {
        List<Employee> employees = employeeRepository.findByBranch_BranchCode(branchCode);
        List<EmployeeDto> employeeDtos = employees.stream()
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE)
                .map(EmployeeMapper::entityToDto)
                .collect(Collectors.toList());
        Collections.reverse(employeeDtos);
        return employeeDtos;

    }

    @Override
    public List<EmployeeDto> findByRole(EmployeeRole role) {
        List<Employee> employees = employeeRepository.findByRole(role);
        List<EmployeeDto> employeeDtos = employees.stream()
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE)
                .map(EmployeeMapper::entityToDto)
                .collect(Collectors.toList());
        Collections.reverse(employeeDtos);
        return employeeDtos;
    }

    @Override
    public List<EmployeeDto> findByBranch_BranchCodeAndRole(String branchCode, EmployeeRole role) {
        List<Employee> employees =
                employeeRepository.findByBranch_BranchCodeAndRole(branchCode, role);
        List<EmployeeDto> employeeDtos = employees.stream()
                .filter(employee -> employee.getStatus() == EmployeeStatus.ACTIVE)
                .map(EmployeeMapper::entityToDto)
                .collect(Collectors.toList());
        Collections.reverse(employeeDtos);
        return employeeDtos;
    }

}
