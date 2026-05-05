package com.autoservice.application.cliente.query;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ClienteDetailOutputTest {

    @Test
    void fromCopiaCamposEUsaVeiculosPassados() {
        var rep = new ClienteOutput.RepresentanteLegalOutput("r1", "Rep", "111", "r@x.com", "11");
        ClienteOutput cliente = new ClienteOutput(
                "cid",
                "PJ",
                LocalDate.of(2020, 1, 2),
                null,
                null,
                "Razão",
                "33444555000100",
                "c@x.com",
                "21999",
                rep
        );
        List<ClienteDetailOutput.VeiculoOutput> veiculos = List.of(
                new ClienteDetailOutput.VeiculoOutput("v1", "ABC", "F", "Ka", 2020, "br", 10_000)
        );

        ClienteDetailOutput out = ClienteDetailOutput.from(cliente, veiculos);

        assertEquals("cid", out.id());
        assertEquals("PJ", out.tipoPessoa());
        assertEquals(LocalDate.of(2020, 1, 2), out.dataCadastro());
        assertEquals("Razão", out.razaoSocial());
        assertEquals("33444555000100", out.cnpj());
        assertEquals(rep, out.representanteLegal());
        assertSame(veiculos, out.veiculos());
    }
}
