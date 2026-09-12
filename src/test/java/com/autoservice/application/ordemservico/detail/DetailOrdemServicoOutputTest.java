package com.autoservice.application.ordemservico.detail;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DetailOrdemServicoOutputTest {

    @Test
    void fromMontaDetalheEValorTotal() {
        final var veiculo = new ListOrdemServicoOutput.VeiculoOutput(
                "v1", "ABC1D23", "Fiat", "Uno", 2015, "Branca", 50000);
        final var cliente = new ListOrdemServicoOutput.ClienteOutput(
                "c1", "FISICA", "Maria", "11111111111", null, null, "m@e.com", "11999999999", null);
        final var list = new ListOrdemServicoOutput(
                "os1",
                "RECEBIDO",
                LocalDate.now(),
                "Relato",
                1,
                0,
                LocalDateTime.now().minusHours(1),
                null,
                veiculo,
                cliente);
        final var pecaOut = new DetailOrdemServicoOutput.PecaOutput("p1", "COD", "Peça X", "tv1");
        final var item = new DetailOrdemServicoOutput.ItemOutput(
                "i1",
                "PECA",
                "Troca",
                null,
                pecaOut,
                2,
                new BigDecimal("15.00"),
                new BigDecimal("30.00"));

        final DetailOrdemServicoOutput detail = DetailOrdemServicoOutput.from(list, List.of(item));

        assertEquals("os1", detail.ordemServicoId());
        assertEquals(new BigDecimal("30.00"), detail.valorTotal());
        assertNotNull(detail.itens());
        assertEquals(1, detail.itens().size());
    }
}
