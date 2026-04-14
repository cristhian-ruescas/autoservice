package com.autoservice.presentation.dto;

public record VehicleDTO(
        String id,
        String plate,
        String brand,
        String model,
        Integer year,
        String customerId
) {
}

