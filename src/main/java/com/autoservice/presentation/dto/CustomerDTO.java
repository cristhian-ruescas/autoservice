package com.autoservice.presentation.dto;

public record CustomerDTO(
        String id,
        String name,
        String phoneNumber,
        AddressDTO address,
        String document,
        String registrationDate
) {
}
