package com.vishal.bankingsystem.employee.mapper;
import com.vishal.bankingsystem.employee.dto.EmployeeDto;
import com.vishal.bankingsystem.employee.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto entityToDto(Employee employee){

        String branchCode = employee.getBranch() != null
                ? employee.getBranch().getBranchCode()
                : null;

        return new EmployeeDto(
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getRole(),
                branchCode
        );
    }

    public static Employee dtoToEntity(EmployeeDto dto){

        Employee employee = new Employee();

        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setRole(dto.getRole());

        return employee;
    }
}