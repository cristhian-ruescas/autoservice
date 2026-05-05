package com.autoservice.application.ordemcompra.query;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrdemCompraOutputTest {

    @Test
    void accessors() {
        var item = new OrdemCompraOutput.ItemOutput("i1", "p1", "P9", "desc", 3);
        OrdemCompraOutput out = new OrdemCompraOutput("oc1", "REALIZADA", LocalDate.of(2024, 6, 1), List.of(item));
        assertEquals("oc1", out.id());
        assertEquals(1, out.itens().size());
        assertEquals("P9", out.itens().getFirst().codigo());
    }
}
