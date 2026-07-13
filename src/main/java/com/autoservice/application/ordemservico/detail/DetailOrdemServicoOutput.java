package com.autoservice.application.ordemservico.detail;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DetailOrdemServicoOutput(
        String ordemServicoId,
        String status,
        LocalDate dataCriacao,
        String relato,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm,
        ListOrdemServicoOutput.VeiculoOutput veiculo,
        ListOrdemServicoOutput.ClienteOutput cliente,
        List<ItemOutput> itens,
        BigDecimal valorTotal
) {

    public static DetailOrdemServicoOutput from(
            final ListOrdemServicoOutput ordemServico,
            final List<ItemOutput> itens
    ) {
        final BigDecimal valorTotal = itens.stream()
                .map(ItemOutput::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DetailOrdemServicoOutput(
                ordemServico.ordemServicoId(),
                ordemServico.status(),
                ordemServico.dataCriacao(),
                ordemServico.relato(),
                ordemServico.tempoPrevistoExecucaoDias(),
                ordemServico.tempoPrevistoExecucaoHoras(),
                ordemServico.iniciadoEm(),
                ordemServico.finalizadoEm(),
                ordemServico.veiculo(),
                ordemServico.cliente(),
                itens,
                valorTotal
        );
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ItemOutput(
            String id,
            String tipo,
            String descricao,
            String pecaId,
            PecaOutput peca,
            Integer quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PecaOutput(
            String id,
            String codigo,
            String descricao,
            String tipoVeiculoId
    ) {
    }
}
