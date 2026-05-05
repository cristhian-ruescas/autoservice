package com.autoservice.application.ordemservico.list;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListOrdemServicoOutputTest {

    @Test
    void representanteLegalEhAninhado() {
        var rep = new ListOrdemServicoOutput.RepresentanteLegalOutput(
                "Rep", "222", "rep@x.com", "21888"
        );
        var cliente = new ListOrdemServicoOutput.ClienteOutput(
                "cid", "PF", "João", "111", null, null, "j@x.com", "21777", rep
        );
        var veiculo = new ListOrdemServicoOutput.VeiculoOutput(
                "vid", "XYZ", "VW", "Gol", 2019, "branco", 120_000
        );

        var out = new ListOrdemServicoOutput(
                "os1",
                "APROVADA",
                LocalDate.now(),
                "relato",
                1,
                2,
                LocalDateTime.now(),
                null,
                veiculo,
                cliente
        );

        assertEquals(rep, out.cliente().representanteLegal());
        assertEquals("João", out.cliente().nome());
    }
}
