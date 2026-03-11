package com.vishal.bankingsystem.customer.service;

import com.vishal.bankingsystem.customer.dto.CustomerDto;
import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.customer.enums.CustomerStatus;
import com.vishal.bankingsystem.customer.mapper.CustomerMapper;
import com.vishal.bankingsystem.customer.repository.CustomerRepository;
import com.vishal.bankingsystem.auth.service.UserAccountStateService;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserAccountStateService userAccountStateService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               UserAccountStateService userAccountStateService){
        this.customerRepository = customerRepository;
        this.userAccountStateService = userAccountStateService;
    }

    @Override
    public CustomerDto createCustomer(CustomerDto dto){
        Optional<Customer> existingCustomer = customerRepository.findByEmail(dto.getEmail());
        Customer customer;

        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            if (customer.getStatus() != CustomerStatus.INACTIVE) {
                throw new ConflictException("Customer already exists and is not inactive");
            }
        } else {
            customer = CustomerMapper.dtoToEntity(dto);
        }

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setCountry(dto.getCountry());
        customer.setPostalCode(dto.getPostalCode());
        customer.setStatus(dto.getStatus() == null ? CustomerStatus.ACTIVE : dto.getStatus());

        Customer saved = customerRepository.save(customer);
        userAccountStateService.syncUserState(saved.getEmail());
        return CustomerMapper.entityToDto(saved);
    }

    @Override
    public CustomerDto getCustomer(String customerNumber){
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return CustomerMapper.entityToDto(customer);
    }

    @Override
    public List<CustomerDto> getAllCustomers(){
        return customerRepository.findAll()
                .stream()
                .filter(customer -> customer.getStatus() == CustomerStatus.ACTIVE)
                .map(CustomerMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(String customerNumber){
        Customer customer = customerRepository.findByCustomerNumber(customerNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setStatus(CustomerStatus.INACTIVE);
        Customer savedCustomer = customerRepository.save(customer);
        userAccountStateService.syncUserState(savedCustomer.getEmail());
    }

    @Override
    public CustomerDto findByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for email: " + email));
        return CustomerMapper.entityToDto(customer);
    }
}
