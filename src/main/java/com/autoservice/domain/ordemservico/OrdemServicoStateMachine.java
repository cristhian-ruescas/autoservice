package com.autoservice.domain.ordemservico;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.TempoPrevistoExecucao;

import java.time.LocalDateTime;

public final class OrdemServicoStateMachine {

    private OrdemServicoStateMachine() {
    }

    public record DiagnosticoFinalizado(
            OrdemServicoStatus status,
            int tempoPrevistoExecucaoDias,
            int tempoPrevistoExecucaoHoras
    ) {
    }

    public record AprovacaoOrcamento(
            OrdemServicoStatus status,
            LocalDateTime iniciadoEm
    ) {
    }

    public static OrdemServicoStatus iniciarDiagnostico(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.exigir(
                statusAtual,
                OrdemServicoStatus.RECEBIDO,
                "Ordem de serviço precisa estar RECEBIDO para iniciar diagnóstico"
        );

        return OrdemServicoStatus.EM_DIAGNOSTICO;
    }

    public static DiagnosticoFinalizado finalizarDiagnostico(
            final OrdemServicoStatus statusAtual,
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras
    ) {
        OrdemServicoStatusGuard.exigir(
                statusAtual,
                OrdemServicoStatus.EM_DIAGNOSTICO,
                "Ordem de serviço precisa estar EM_DIAGNOSTICO para finalizar diagnóstico"
        );

        final var tempoPrevisto = TempoPrevistoExecucao.of(
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras
        );

        return new DiagnosticoFinalizado(
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
                tempoPrevisto.dias(),
                tempoPrevisto.horas()
        );
    }

    public static AprovacaoOrcamento aprovarOrcamento(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.exigir(
                statusAtual,
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
                "Ordem de serviço precisa estar AGUARDANDO_APROVACAO para aprovar orçamento"
        );

        return new AprovacaoOrcamento(OrdemServicoStatus.EM_EXECUCAO, LocalDateTime.now());
    }

    public static OrdemServicoStatus reprovarOrcamento(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.exigir(
                statusAtual,
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
                "Ordem de serviço precisa estar AGUARDANDO_APROVACAO para reprovar orçamento"
        );

        return OrdemServicoStatus.REPROVADO;
    }

    public static OrdemServicoStatus finalizarExecucao(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.exigir(
                statusAtual,
                OrdemServicoStatus.EM_EXECUCAO,
                "Ordem de serviço precisa estar EM_EXECUCAO para ser finalizada"
        );

        return OrdemServicoStatus.FINALIZADA;
    }

    public static OrdemServicoStatus entregar(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.exigirUmDe(
                statusAtual,
                "Ordem de serviço precisa estar FINALIZADA ou REPROVADO para ser entregue",
                OrdemServicoStatus.FINALIZADA,
                OrdemServicoStatus.REPROVADO
        );

        return OrdemServicoStatus.ENTREGUE;
    }

    public static OrdemServicoStatus cancelar(final OrdemServicoStatus statusAtual) {
        OrdemServicoStatusGuard.proibir(
                statusAtual,
                "Ordem de serviço FINALIZADA, ENTREGUE ou REPROVADO não pode ser cancelada",
                OrdemServicoStatus.FINALIZADA,
                OrdemServicoStatus.ENTREGUE,
                OrdemServicoStatus.REPROVADO
        );

        return OrdemServicoStatus.CANCELADO;
    }
}
