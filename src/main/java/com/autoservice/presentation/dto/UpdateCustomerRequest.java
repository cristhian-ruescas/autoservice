package com.autoservice.presentation.dto;

import com.autoservice.validation.ValidCpfOrCnpj;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequest(
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotNull AddressDTO address,
        @ValidCpfOrCnpj @NotBlank String document,
        @NotBlank String registrationDate
) {
}
