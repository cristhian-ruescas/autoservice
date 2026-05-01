package com.autoservice.presentation.controller;

import com.autoservice.application.CustomerService;
import com.autoservice.domain.customer.*;
import com.autoservice.presentation.dto.CreateCustomerRequest;
import com.autoservice.presentation.dto.CustomerDTO;
import com.autoservice.presentation.dto.UpdateCustomerRequest;
import com.autoservice.presentation.mapper.CustomerMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDate.parse;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customer = customerService.create(
                request.name(),
                PhoneNumber.from(request.phoneNumber()),
                CustomerMapper.toAddress(request.address()),
                Document.from(request.document()),
                RegistrationDate.from(parse(request.registrationDate()))
        );
        CustomerDTO dto = CustomerMapper.toDTO(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable String id) {
        Optional<Customer> customer = customerService.findById(CustomerID.from(id));
        return customer.map(c -> ResponseEntity.ok(CustomerMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        List<Customer> customers = customerService.findAll();
        List<CustomerDTO> dtos = customers.stream().map(CustomerMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable String id, @Valid @RequestBody UpdateCustomerRequest request) {
        Customer customer = customerService.update(
                CustomerID.from(id),
                request.name(),
                PhoneNumber.from(request.phoneNumber()),
                CustomerMapper.toAddress(request.address()),
                Document.from(request.document()),
                RegistrationDate.from(parse(request.registrationDate()))
        );
        CustomerDTO dto = CustomerMapper.toDTO(customer);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        customerService.delete(CustomerID.from(id));
        return ResponseEntity.noContent().build();
    }
}
