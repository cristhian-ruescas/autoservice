package com.autoservice.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateWorkOrderRequest(
        @NotBlank String clienteId,
        @NotBlank String veiculoId,
        @NotEmpty List<String> servicoIds,
        @Valid List<WorkOrderPartInputDTO> pecas
) {
}

