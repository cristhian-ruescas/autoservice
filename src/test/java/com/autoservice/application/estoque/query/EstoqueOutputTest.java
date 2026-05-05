package com.autoservice.application.estoque.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstoqueOutputTest {

    @Test
    void accessors() {
        var peca = new EstoqueOutput.PecaOutput("pid", "P001", "filtro", "Mann");
        EstoqueOutput out = new EstoqueOutput("eid", 10, 2, "A1", peca);
        assertEquals("eid", out.id());
        assertEquals(10, out.quantidadeDisponivel());
        assertEquals(peca, out.peca());
    }
}
