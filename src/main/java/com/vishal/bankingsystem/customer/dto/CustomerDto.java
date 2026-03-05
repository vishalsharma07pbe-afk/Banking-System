package com.vishal.bankingsystem.customer.dto;

import com.vishal.bankingsystem.customer.enums.CustomerStatus;

public class CustomerDto {

    private String customerNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private CustomerStatus status;

    public CustomerDto(String customerNumber,
                       String firstName,
                       String lastName,
                       String email,
                       String phoneNumber,
                       String city,
                       String state,
                       String country,
                       String postalCode,
                       CustomerStatus status) {

        this.customerNumber = customerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.status = status;
    }

    public String getCustomerNumber() { return customerNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getPostalCode() { return postalCode; }
    public CustomerStatus getStatus() { return status; }
}