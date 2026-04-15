package com.autoservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequest(
    @NotBlank String nome,
    @NotBlank String telefone,
    @NotNull AddressDTO endereco,
    @NotBlank String documento,
    @NotBlank String dataRegistro
) {}
