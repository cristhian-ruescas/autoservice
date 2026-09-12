package com.autoservice.application.atendimento.create;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.veiculo.Veiculo;

public record AbrirAtendimentoOutput(
        String clienteId,
        String veiculoId,
        String ordemServicoId,
        String status
) {
    public static AbrirAtendimentoOutput from(
            final Pessoa pessoa,
            final Cliente cliente,
            final Veiculo veiculo,
            final OrdemServico ordemServico
    ) {
        return new AbrirAtendimentoOutput(
                cliente.getId().getValue(),
                veiculo.getId().getValue(),
                ordemServico.getId().getValue(),
                ordemServico.getStatus().name()
        );
    }
}
