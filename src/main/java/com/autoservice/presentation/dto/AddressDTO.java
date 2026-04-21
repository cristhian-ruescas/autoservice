package com.autoservice.presentation.dto;

public record AddressDTO(
    String rua,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String estado,
    String cep
) {}
