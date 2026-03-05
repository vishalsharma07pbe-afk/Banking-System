package com.vishal.bankingsystem.customer.controller;

import com.vishal.bankingsystem.customer.dto.CustomerDto;
import com.vishal.bankingsystem.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping
    public CustomerDto createCustomer(@Valid @RequestBody CustomerDto dto){
        return customerService.createCustomer(dto);
    }

    @GetMapping("/{customerNumber}")
    public CustomerDto getCustomer(@PathVariable String customerNumber){
        return customerService.getCustomer(customerNumber);
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @DeleteMapping("/{customerNumber}")
    public void deleteCustomer(@PathVariable String customerNumber){
        customerService.deleteCustomer(customerNumber);
    }

    @GetMapping("/email/{email}")
    public CustomerDto getCustomerByEmail(@PathVariable String email){
        return customerService.findByEmail(email);
    }
}
