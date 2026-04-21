package com.autoservice.presentation.dto;

public record CustomerDTO(
    String id,
    String nome,
    String telefone,
    AddressDTO endereco,
    String documento,
    String dataRegistro
) {}
