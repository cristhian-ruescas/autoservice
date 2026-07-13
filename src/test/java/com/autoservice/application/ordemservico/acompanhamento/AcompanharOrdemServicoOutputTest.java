package com.autoservice.application.ordemservico.acompanhamento;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AcompanharOrdemServicoOutput")
class AcompanharOrdemServicoOutputTest {

    @Test
    @DisplayName("Deve montar andamento para ordem em execucao")
    void deveMontarAndamentoParaOrdemEmExecucao() {
        final var iniciadoEm = LocalDateTime.of(2026, 5, 3, 10, 0);
        final var veiculo = new ListOrdemServicoOutput.VeiculoOutput(
                "veiculo-id",
                "ABC1D23",
                "Fiat",
                "Uno",
                2020,
                "Prata",
                55000
        );
        final var ordemServico = new ListOrdemServicoOutput(
                "ordem-id",
                "EM_EXECUCAO",
                LocalDate.of(2026, 5, 3),
                "Cliente relata barulho ao frear",
                1,
                2,
                iniciadoEm,
                null,
                veiculo,
                null
        );

        final var output = AcompanharOrdemServicoOutput.from(ordemServico);

        assertEquals("ordem-id", output.ordemServicoId());
        assertEquals("EM_EXECUCAO", output.status());
        assertEquals("Em Execução", output.descricaoStatus());
        assertEquals("Execucao", output.etapaAtual());
        assertEquals(75, output.percentualAndamento());
        assertEquals(iniciadoEm, output.iniciadoEm());
        assertEquals(6, output.etapas().size());
        assertEquals("CONCLUIDA", output.etapas().get(2).situacao());
        assertEquals("ATUAL", output.etapas().get(3).situacao());
        assertEquals("PENDENTE", output.etapas().get(4).situacao());
    }

    @Test
    @DisplayName("Deve montar andamento para ordem reprovada")
    void deveMontarAndamentoParaOrdemReprovada() {
        final var ordemServico = new ListOrdemServicoOutput(
                "ordem-id",
                "REPROVADO",
                LocalDate.of(2026, 5, 3),
                "Cliente relata barulho ao frear",
                null,
                null,
                null,
                null,
                null,
                null
        );

        final var output = AcompanharOrdemServicoOutput.from(ordemServico);

        assertEquals("REPROVADO", output.status());
        assertEquals("Reprovado", output.etapaAtual());
        assertEquals(100, output.percentualAndamento());
        assertEquals(1, output.etapas().size());
        assertEquals("ATUAL", output.etapas().getFirst().situacao());
    }

    @Test
    @DisplayName("Deve lançar DomainException para status inválido")
    void deveLancarDomainExceptionParaStatusInvalido() {
        final var ordemServico = new ListOrdemServicoOutput(
                "ordem-id",
                "INVALIDO",
                LocalDate.of(2026, 5, 3),
                "Cliente relata barulho ao frear",
                null,
                null,
                null,
                null,
                null,
                null
        );

        final var exception = assertThrows(
                DomainException.class,
                () -> AcompanharOrdemServicoOutput.from(ordemServico)
        );

        assertEquals("Status da ordem de serviço inválido para acompanhamento", exception.getMessage());
    }
}
