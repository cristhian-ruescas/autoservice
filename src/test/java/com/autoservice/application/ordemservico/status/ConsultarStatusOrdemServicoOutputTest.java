package com.autoservice.application.ordemservico.status;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ConsultarStatusOrdemServicoOutput")
class ConsultarStatusOrdemServicoOutputTest {

    @Test
    void deveMontarConsultaDeStatus() {
        final var ordem = new ListOrdemServicoOutput(
                "ordem-id",
                "EM_EXECUCAO",
                LocalDate.of(2026, 5, 3),
                "Relato",
                1,
                2,
                LocalDateTime.of(2026, 5, 3, 10, 0),
                null,
                null,
                null
        );

        final var output = ConsultarStatusOrdemServicoOutput.from(ordem);

        assertEquals("ordem-id", output.ordemServicoId());
        assertEquals("EM_EXECUCAO", output.status());
        assertEquals("Em Execução", output.descricaoStatus());
    }
}
