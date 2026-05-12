package com.autoservice.domain.ordemservico.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("OrdemServicoStatus")
class OrdemServicoStatusTest {

    @Test
    @DisplayName("Deve verificar as descrições dos status")
    void deveVerificarDescricoes() {
        assertEquals("Recebido", OrdemServicoStatus.RECEBIDO.getDescricao());
        assertEquals("Em Diagnóstico", OrdemServicoStatus.EM_DIAGNOSTICO.getDescricao());
        assertEquals("Aguardando Aprovação", OrdemServicoStatus.AGUARDANDO_APROVACAO.getDescricao());
        assertEquals("Aprovado", OrdemServicoStatus.APROVADO.getDescricao());
        assertEquals("Reprovado", OrdemServicoStatus.REPROVADO.getDescricao());
        assertEquals("Em Execução", OrdemServicoStatus.EM_EXECUCAO.getDescricao());
        assertEquals("Finalizada", OrdemServicoStatus.FINALIZADA.getDescricao());
        assertEquals("Entregue", OrdemServicoStatus.ENTREGUE.getDescricao());
        assertEquals("Cancelado", OrdemServicoStatus.CANCELADO.getDescricao());
    }

    @Test
    @DisplayName("Deve garantir que todos os valores do enum existam")
    void deveGarantirValores() {
        OrdemServicoStatus[] values = OrdemServicoStatus.values();
        assertEquals(9, values.length);

        assertNotNull(OrdemServicoStatus.valueOf("RECEBIDO"));
        assertNotNull(OrdemServicoStatus.valueOf("EM_DIAGNOSTICO"));
    }
}
