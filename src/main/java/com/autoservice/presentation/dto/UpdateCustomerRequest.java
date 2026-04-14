package com.autoservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequest(
    @NotBlank String name,
    @NotBlank String phoneNumber,
    @NotNull AddressDTO address,
    @NotBlank String document,
    @NotBlank String registrationDate
) {}
