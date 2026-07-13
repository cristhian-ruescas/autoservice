package com.autoservice.domain.ordemservico;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OrdemServicoPrioridadeMonitoramento")
class OrdemServicoPrioridadeMonitoramentoTest {

    @Test
    @DisplayName("Deve priorizar status operacionais conforme Fase 2")
    void devePriorizarStatusOperacionais() {
        assertTrue(OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.EM_EXECUCAO) < OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.AGUARDANDO_APROVACAO));
        assertTrue(OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.AGUARDANDO_APROVACAO) < OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.EM_DIAGNOSTICO));
        assertTrue(OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.EM_DIAGNOSTICO) < OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.RECEBIDO));
        assertTrue(OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.RECEBIDO) < OrdemServicoPrioridadeMonitoramento.prioridade(OrdemServicoStatus.REPROVADO));
    }

    @ParameterizedTest
    @EnumSource(value = OrdemServicoStatus.class, names = {"FINALIZADA", "ENTREGUE"})
    @DisplayName("Deve excluir ordens finalizadas ou entregues da listagem operacional")
    void deveExcluirOrdensEncerradas(final OrdemServicoStatus status) {
        assertTrue(OrdemServicoPrioridadeMonitoramento.excluidoDaListagemOperacional(status));
    }

    @ParameterizedTest
    @EnumSource(value = OrdemServicoStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"FINALIZADA", "ENTREGUE"})
    @DisplayName("Deve incluir demais status na listagem operacional")
    void deveIncluirDemaisStatus(final OrdemServicoStatus status) {
        assertFalse(OrdemServicoPrioridadeMonitoramento.excluidoDaListagemOperacional(status));
    }
}
