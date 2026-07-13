package com.autoservice.domain.itemservico.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemServicoTipoTest {

    @Test
    void valores() {
        assertEquals(2, ItemServicoTipo.values().length);
        assertEquals(ItemServicoTipo.SERVICO, ItemServicoTipo.valueOf("SERVICO"));
        assertEquals(ItemServicoTipo.PECA, ItemServicoTipo.valueOf("PECA"));
    }
}
