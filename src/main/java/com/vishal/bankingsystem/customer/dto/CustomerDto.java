package com.vishal.bankingsystem.customer.dto;

import com.vishal.bankingsystem.customer.enums.CustomerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomerDto {
    @NotBlank(message = "Customer number is required")
    private String customerNumber;
    @NotBlank(message = "First name is required")
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Postal code is required")
    private String postalCode;
    @NotNull(message = "Customer status is required")
    private CustomerStatus status;

    public CustomerDto() {
    }

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

    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setStatus(CustomerStatus status) { this.status = status; }
}
