package com.autoservice.domain.ordemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrdemServicoStateMachine")
class OrdemServicoStateMachineTest {

    @Test
    void deveIniciarDiagnosticoQuandoRecebido() {
        assertEquals(
                OrdemServicoStatus.EM_DIAGNOSTICO,
                OrdemServicoStateMachine.iniciarDiagnostico(OrdemServicoStatus.RECEBIDO)
        );
    }

    @Test
    void deveFalharAoIniciarDiagnosticoForaDeRecebido() {
        final var exception = assertThrows(
                DomainException.class,
                () -> OrdemServicoStateMachine.iniciarDiagnostico(OrdemServicoStatus.EM_EXECUCAO)
        );

        assertEquals(
                "Ordem de serviço precisa estar RECEBIDO para iniciar diagnóstico",
                exception.getErrors().getFirst().message()
        );
    }

    @Test
    void deveFinalizarDiagnosticoComTempoPrevisto() {
        final var resultado = OrdemServicoStateMachine.finalizarDiagnostico(
                OrdemServicoStatus.EM_DIAGNOSTICO,
                1,
                2
        );

        assertEquals(OrdemServicoStatus.AGUARDANDO_APROVACAO, resultado.status());
        assertEquals(1, resultado.tempoPrevistoExecucaoDias());
        assertEquals(2, resultado.tempoPrevistoExecucaoHoras());
    }

    @Test
    void deveAprovarOrcamento() {
        final var resultado = OrdemServicoStateMachine.aprovarOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO);

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, resultado.status());
    }

    @Test
    void deveReprovarOrcamento() {
        assertEquals(
                OrdemServicoStatus.REPROVADO,
                OrdemServicoStateMachine.reprovarOrcamento(OrdemServicoStatus.AGUARDANDO_APROVACAO)
        );
    }

    @Test
    void deveFinalizarExecucao() {
        assertEquals(
                OrdemServicoStatus.FINALIZADA,
                OrdemServicoStateMachine.finalizarExecucao(OrdemServicoStatus.EM_EXECUCAO)
        );
    }

    @Test
    void deveEntregarQuandoFinalizadaOuReprovada() {
        assertEquals(
                OrdemServicoStatus.ENTREGUE,
                OrdemServicoStateMachine.entregar(OrdemServicoStatus.FINALIZADA)
        );
        assertEquals(
                OrdemServicoStatus.ENTREGUE,
                OrdemServicoStateMachine.entregar(OrdemServicoStatus.REPROVADO)
        );
    }

    @Test
    void deveCancelarQuandoPermitido() {
        assertEquals(
                OrdemServicoStatus.CANCELADO,
                OrdemServicoStateMachine.cancelar(OrdemServicoStatus.RECEBIDO)
        );
    }

    @Test
    void deveFalharAoCancelarOrdemEntregue() {
        assertThrows(
                DomainException.class,
                () -> OrdemServicoStateMachine.cancelar(OrdemServicoStatus.ENTREGUE)
        );
    }
}
