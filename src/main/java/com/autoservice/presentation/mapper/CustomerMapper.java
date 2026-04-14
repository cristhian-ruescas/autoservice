package com.autoservice.presentation.mapper;

import com.autoservice.domain.customer.*;
import com.autoservice.presentation.dto.*;

import java.time.LocalDate;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        AddressDTO addressDTO = new AddressDTO(
            customer.getAddress().getStreet(),
            customer.getAddress().getNumber(),
            customer.getAddress().getComplement(),
            customer.getAddress().getNeighborhood(),
            customer.getAddress().getCity(),
            customer.getAddress().getState().name(),
            customer.getAddress().getZipCode().getValue()
        );
        return new CustomerDTO(
            customer.getId().getValue(),
            customer.getName(),
            customer.getPhoneNumber().getValue(),
            addressDTO,
            customer.getDocument().getValue(),
            customer.getRegistrationDate().getValue().toString()
        );
    }

    public static Address toAddress(AddressDTO dto) {
        return Address.from(
            dto.street(),
            dto.number(),
            dto.complement(),
            dto.neighborhood(),
            dto.city(),
            State.valueOf(dto.state()),
            ZipCode.from(dto.zipCode())
        );
    }

    public static Customer toCustomer(CreateCustomerRequest request) {
        Address address = toAddress(request.address());
        return Customer.newCustomer(
            request.name(),
            PhoneNumber.from(request.phoneNumber()),
            address,
            Document.from(request.document()),
            RegistrationDate.from(LocalDate.parse(request.registrationDate()))
        );
    }

    public static Customer toCustomer(String id, UpdateCustomerRequest request) {
        Address address = toAddress(request.address());
        return Customer.withId(
            CustomerID.from(id),
            request.name(),
            PhoneNumber.from(request.phoneNumber()),
            address,
            Document.from(request.document()),
            RegistrationDate.from(LocalDate.parse(request.registrationDate()))
        );
    }
}
