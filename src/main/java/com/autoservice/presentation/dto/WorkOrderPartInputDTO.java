package com.autoservice.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkOrderPartInputDTO(
        @NotBlank String partId,
        @NotNull @Min(1) Integer quantity
) {
}

