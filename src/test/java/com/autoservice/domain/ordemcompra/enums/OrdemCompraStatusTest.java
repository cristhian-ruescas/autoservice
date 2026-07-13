package com.autoservice.domain.ordemcompra.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdemCompraStatusTest {

    @Test
    void descricoes() {
        assertEquals("Pendente", OrdemCompraStatus.PENDENTE.getDescricao());
        assertEquals("Realizado", OrdemCompraStatus.REALIZADO.getDescricao());
    }
}
