package com.vishal.bankingsystem.customer.controller;

import com.vishal.bankingsystem.customer.dto.CustomerDto;
import com.vishal.bankingsystem.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN')")
    @PostMapping
    public CustomerDto createCustomer(@Valid @RequestBody CustomerDto dto){
        return customerService.createCustomer(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','SUPPORT_AGENT','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR','COMPLIANCE_OFFICER','FRAUD_ANALYST')")
    @GetMapping("/{customerNumber}")
    public CustomerDto getCustomer(@PathVariable String customerNumber){
        return customerService.getCustomer(customerNumber);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','SUPPORT_AGENT','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR','COMPLIANCE_OFFICER','FRAUD_ANALYST')")
    @GetMapping
    public List<CustomerDto> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{customerNumber}")
    public void deleteCustomer(@PathVariable String customerNumber){
        customerService.deleteCustomer(customerNumber);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','SUPPORT_AGENT','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR','COMPLIANCE_OFFICER','FRAUD_ANALYST')")
    @GetMapping("/email/{email}")
    public CustomerDto getCustomerByEmail(@PathVariable String email){
        return customerService.findByEmail(email);
    }
}
