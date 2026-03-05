package com.vishal.bankingsystem.customer.service;

import com.vishal.bankingsystem.customer.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto getCustomer(String customerNumber);
    List<CustomerDto> getAllCustomers();
    void deleteCustomer(String customerNumber);
    CustomerDto findByEmail(String email);
}