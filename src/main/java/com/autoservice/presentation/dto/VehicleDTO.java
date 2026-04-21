package com.autoservice.presentation.dto;

public record VehicleDTO(
        String id,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String clienteId
) {
}

