package com.autoservice.domain.ordemservico.acompanhamento;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.validation.Error;

import java.util.List;

public final class OrdemServicoAcompanhamentoCalculator {

    private static final List<Etapa> ETAPAS = List.of(
            new Etapa(OrdemServicoStatus.RECEBIDO, "Recebida", 10),
            new Etapa(OrdemServicoStatus.EM_DIAGNOSTICO, "Diagnostico", 30),
            new Etapa(OrdemServicoStatus.AGUARDANDO_APROVACAO, "Aguardando aprovacao", 50),
            new Etapa(OrdemServicoStatus.EM_EXECUCAO, "Execucao", 75),
            new Etapa(OrdemServicoStatus.FINALIZADA, "Finalizacao", 90),
            new Etapa(OrdemServicoStatus.ENTREGUE, "Entrega", 100)
    );

    private OrdemServicoAcompanhamentoCalculator() {
    }

    public static Resultado calcular(final String status) {
        final var statusNormalizado = statusOf(status);
        final var etapaAtual = etapaAtual(statusNormalizado);

        return new Resultado(
                statusNormalizado,
                etapaAtual.nome(),
                percentualAndamento(statusNormalizado),
                etapas(statusNormalizado)
        );
    }

    public static OrdemServicoStatus statusOf(final String status) {
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
            case AGUARDANDO_APROVACAO -> 50;
            case EM_EXECUCAO -> 75;
            case FINALIZADA -> 90;
            case ENTREGUE, REPROVADO, CANCELADO -> 100;
        };
    }

    private static Etapa etapaAtual(final OrdemServicoStatus status) {
        if (status == OrdemServicoStatus.REPROVADO || status == OrdemServicoStatus.CANCELADO) {
            return new Etapa(status, status.getDescricao(), 100);
        }

        return etapaByStatus(status);
    }

    private static List<EtapaProgresso> etapas(final OrdemServicoStatus status) {
        if (status == OrdemServicoStatus.REPROVADO || status == OrdemServicoStatus.CANCELADO) {
            return List.of(new EtapaProgresso(
                    status.name(),
                    status.getDescricao(),
                    "ATUAL",
                    100
            ));
        }

        final var percentualAtual = percentualAndamento(status);

        return ETAPAS.stream()
                .map(etapa -> new EtapaProgresso(
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
        if (etapa.status() == status) {
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

    public record Resultado(
            OrdemServicoStatus status,
            String etapaAtual,
            int percentualAndamento,
            List<EtapaProgresso> etapas
    ) {
    }

    public record EtapaProgresso(
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
