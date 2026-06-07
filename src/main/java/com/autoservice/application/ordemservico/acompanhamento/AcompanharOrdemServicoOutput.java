package com.autoservice.application.ordemservico.acompanhamento;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.acompanhamento.OrdemServicoAcompanhamentoCalculator;
import com.autoservice.validation.Error;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AcompanharOrdemServicoOutput(
        String ordemServicoId,
        String status,
        String descricaoStatus,
        String etapaAtual,
        int percentualAndamento,
        LocalDate dataCriacao,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm,
        ListOrdemServicoOutput.VeiculoOutput veiculo,
        List<EtapaOutput> etapas
) {

    public static AcompanharOrdemServicoOutput from(final ListOrdemServicoOutput ordemServico) {
        if (ordemServico == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para acompanhamento"));
        }

        final var progresso = OrdemServicoAcompanhamentoCalculator.calcular(ordemServico.status());

        return new AcompanharOrdemServicoOutput(
                ordemServico.ordemServicoId(),
                progresso.status().name(),
                progresso.status().getDescricao(),
                progresso.etapaAtual(),
                progresso.percentualAndamento(),
                ordemServico.dataCriacao(),
                ordemServico.iniciadoEm(),
                ordemServico.finalizadoEm(),
                ordemServico.veiculo(),
                mapEtapas(progresso.etapas())
        );
    }

    private static List<EtapaOutput> mapEtapas(
            final List<OrdemServicoAcompanhamentoCalculator.EtapaProgresso> etapas
    ) {
        return etapas.stream()
                .map(etapa -> new EtapaOutput(
                        etapa.status(),
                        etapa.nome(),
                        etapa.situacao(),
                        etapa.percentual()
                ))
                .toList();
    }

    public record EtapaOutput(
            String status,
            String nome,
            String situacao,
            int percentual
    ) {
    }
}
