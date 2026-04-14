package com.autoservice.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateWorkOrderRequest(
        @NotBlank String customerId,
        @NotBlank String vehicleId,
        @NotEmpty List<String> serviceIds,
        @Valid List<WorkOrderPartInputDTO> parts
) {
}

