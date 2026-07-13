package com.autoservice.presentation.dto;

import java.util.UUID;

public record CreateOrdemServicoRequest(
        UUID veiculoId,
        String relato
) {
}
