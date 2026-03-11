package com.vishal.bankingsystem.customer.service;

import com.vishal.bankingsystem.customer.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);
    CustomerDto getCustomerByCustomerNumber(String customerNumber);
    List<CustomerDto> getAllCustomers();
    void deactivateCustomer(String customerNumber);
    CustomerDto getCustomerByEmail(String email);
}
