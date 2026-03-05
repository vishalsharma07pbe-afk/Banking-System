package com.vishal.bankingsystem.customer.mapper;

import com.vishal.bankingsystem.customer.dto.CustomerDto;
import com.vishal.bankingsystem.customer.entity.Customer;

public class CustomerMapper {

    public static CustomerDto entityToDto(Customer customer){

        return new CustomerDto(
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getCity(),
                customer.getState(),
                customer.getCountry(),
                customer.getPostalCode(),
                customer.getStatus()
        );
    }

    public static Customer dtoToEntity(CustomerDto dto){

        Customer customer = new Customer();

        customer.setCustomerNumber(dto.getCustomerNumber());
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setCountry(dto.getCountry());
        customer.setPostalCode(dto.getPostalCode());
        customer.setStatus(dto.getStatus());

        return customer;
    }
}