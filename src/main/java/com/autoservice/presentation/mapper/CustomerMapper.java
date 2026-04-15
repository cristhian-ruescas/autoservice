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
            dto.rua(),
            dto.numero(),
            dto.complemento(),
            dto.bairro(),
            dto.cidade(),
            State.valueOf(dto.estado()),
            ZipCode.from(dto.cep())
        );
    }

    public static Customer toCustomer(CreateCustomerRequest request) {
        Address address = toAddress(request.endereco());
        return Customer.newCustomer(
            request.nome(),
            PhoneNumber.from(request.telefone()),
            address,
            Document.from(request.documento()),
            RegistrationDate.from(LocalDate.parse(request.dataRegistro()))
        );
    }

    public static Customer toCustomer(String id, UpdateCustomerRequest request) {
        Address address = toAddress(request.endereco());
        return Customer.withId(
            CustomerID.from(id),
            request.nome(),
            PhoneNumber.from(request.telefone()),
            address,
            Document.from(request.documento()),
            RegistrationDate.from(LocalDate.parse(request.dataRegistro()))
        );
    }
}
