package com.autoservice.presentation.dto;

public record AddressDTO(
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state,
    String zipCode
) {}
