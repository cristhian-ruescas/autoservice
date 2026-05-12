package com.autoservice.application.ordemservico.acompanhamento;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
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

    private static final List<Etapa> ETAPAS = List.of(
            new Etapa(OrdemServicoStatus.RECEBIDO, "Recebimento", 10),
            new Etapa(OrdemServicoStatus.EM_DIAGNOSTICO, "Diagnostico", 30),
            new Etapa(OrdemServicoStatus.AGUARDANDO_APROVACAO, "Aguardando aprovacao", 50),
            new Etapa(OrdemServicoStatus.EM_EXECUCAO, "Execucao", 75),
            new Etapa(OrdemServicoStatus.FINALIZADA, "Finalizacao", 90),
            new Etapa(OrdemServicoStatus.ENTREGUE, "Entrega", 100)
    );

    public static AcompanharOrdemServicoOutput from(final ListOrdemServicoOutput ordemServico) {
        if (ordemServico == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para acompanhamento"));
        }

        final var status = statusOf(ordemServico.status());
        final var etapaAtual = etapaAtual(status);

        return new AcompanharOrdemServicoOutput(
                ordemServico.ordemServicoId(),
                status.name(),
                status.getDescricao(),
                etapaAtual.nome(),
                percentualAndamento(status),
                ordemServico.dataCriacao(),
                ordemServico.iniciadoEm(),
                ordemServico.finalizadoEm(),
                ordemServico.veiculo(),
                etapas(status)
        );
    }

    private static OrdemServicoStatus statusOf(final String status) {
        if (status == null || status.isBlank()) {
            throw DomainException.with(new Error("Status da ordem de serviço é obrigatório para acompanhamento"));
        }

        try {
            return OrdemServicoStatus.valueOf(status);
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("Status da ordem de serviço inválido para acompanhamento"));
        }
    }

    private static int percentualAndamento(final OrdemServicoStatus status) {
        return switch (status) {
            case RECEBIDO -> 10;
            case EM_DIAGNOSTICO -> 30;
            case AGUARDANDO_APROVACAO, APROVADO -> 50;
            case EM_EXECUCAO -> 75;
            case FINALIZADA -> 90;
            case ENTREGUE, REPROVADO, CANCELADO -> 100;
        };
    }

    private static Etapa etapaAtual(final OrdemServicoStatus status) {
        if (status == OrdemServicoStatus.APROVADO) {
            return etapaByStatus(OrdemServicoStatus.AGUARDANDO_APROVACAO);
        }
        if (status == OrdemServicoStatus.REPROVADO || status == OrdemServicoStatus.CANCELADO) {
            return new Etapa(status, status.getDescricao(), 100);
        }

        return etapaByStatus(status);
    }

    private static List<EtapaOutput> etapas(final OrdemServicoStatus status) {
        if (status == OrdemServicoStatus.REPROVADO || status == OrdemServicoStatus.CANCELADO) {
            final var interrupcao = new EtapaOutput(
                    status.name(),
                    status.getDescricao(),
                    "ATUAL",
                    100
            );

            return List.of(interrupcao);
        }

        final var percentualAtual = percentualAndamento(status);

        return ETAPAS.stream()
                .map(etapa -> new EtapaOutput(
                        etapa.status().name(),
                        etapa.nome(),
                        statusEtapa(etapa, status, percentualAtual),
                        etapa.percentual()
                ))
                .toList();
    }

    private static String statusEtapa(
            final Etapa etapa,
            final OrdemServicoStatus status,
            final int percentualAtual
    ) {
        if (etapa.status() == status || status == OrdemServicoStatus.APROVADO
                && etapa.status() == OrdemServicoStatus.AGUARDANDO_APROVACAO) {
            return "ATUAL";
        }
        if (etapa.percentual() < percentualAtual) {
            return "CONCLUIDA";
        }

        return "PENDENTE";
    }

    private static Etapa etapaByStatus(final OrdemServicoStatus status) {
        return ETAPAS.stream()
                .filter(etapa -> etapa.status() == status)
                .findFirst()
                .orElseGet(() -> new Etapa(status, status.getDescricao(), percentualAndamento(status)));
    }

    public record EtapaOutput(
            String status,
            String nome,
            String situacao,
            int percentual
    ) {
    }

    private record Etapa(
            OrdemServicoStatus status,
            String nome,
            int percentual
    ) {
    }
}
