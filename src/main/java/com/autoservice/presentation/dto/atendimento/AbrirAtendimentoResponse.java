package com.autoservice.presentation.dto.atendimento;

import com.autoservice.application.atendimento.create.AbrirAtendimentoOutput;

public record AbrirAtendimentoResponse(
        String clienteId,
        String veiculoId,
        String ordemServicoId,
        String status
) {
    public static AbrirAtendimentoResponse from(final AbrirAtendimentoOutput output) {
        return new AbrirAtendimentoResponse(
                output.clienteId(),
                output.veiculoId(),
                output.ordemServicoId(),
                output.status()
        );
    }
}
