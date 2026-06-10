package com.autoservice.domain.ordemservico.acompanhamento;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("OrdemServicoAcompanhamentoCalculator")
class OrdemServicoAcompanhamentoCalculatorTest {

    @Test
    @DisplayName("Deve calcular andamento para ordem em execucao")
    void deveCalcularAndamentoParaOrdemEmExecucao() {
        final var resultado = OrdemServicoAcompanhamentoCalculator.calcular("EM_EXECUCAO");

        assertEquals(OrdemServicoStatus.EM_EXECUCAO, resultado.status());
        assertEquals("Execucao", resultado.etapaAtual());
        assertEquals(75, resultado.percentualAndamento());
        assertEquals(6, resultado.etapas().size());
        assertEquals("CONCLUIDA", resultado.etapas().get(2).situacao());
        assertEquals("ATUAL", resultado.etapas().get(3).situacao());
        assertEquals("PENDENTE", resultado.etapas().get(4).situacao());
    }

    @Test
    @DisplayName("Deve calcular andamento para ordem reprovada")
    void deveCalcularAndamentoParaOrdemReprovada() {
        final var resultado = OrdemServicoAcompanhamentoCalculator.calcular("REPROVADO");

        assertEquals(OrdemServicoStatus.REPROVADO, resultado.status());
        assertEquals("Reprovado", resultado.etapaAtual());
        assertEquals(100, resultado.percentualAndamento());
        assertEquals(1, resultado.etapas().size());
        assertEquals("ATUAL", resultado.etapas().getFirst().situacao());
    }
}
