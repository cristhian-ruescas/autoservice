package com.autoservice.application;

import com.autoservice.domain.customer.*;
import com.autoservice.domain.customer.CustomerRepository;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer create(String name, PhoneNumber phoneNumber, Address address, Document document, RegistrationDate registrationDate) {
        final Customer customer = Customer.newCustomer(name, phoneNumber, address, document, registrationDate);
        final ValidationHandler handler = new ThrowsValidationHandler();
        customer.validate(handler);
        return customerRepository.save(customer);
    }

    public Optional<Customer> findById(CustomerID id) {
        return customerRepository.findById(id);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer update(CustomerID id, String name, PhoneNumber phoneNumber, Address address, Document document, RegistrationDate registrationDate) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        final Customer updated = Customer.withId(id, name, phoneNumber, address, document, registrationDate);
        final ValidationHandler handler = new ThrowsValidationHandler();
        updated.validate(handler);
        return customerRepository.save(updated);
    }

    @Transactional
    public void delete(CustomerID id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.deleteById(id);
    }
}
