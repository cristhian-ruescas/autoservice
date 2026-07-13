package com.autoservice.domain.ordemservico.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrdemServicoValidator")
class OrdemServicoValidatorTest {

    @Test
    @DisplayName("Deve rejeitar Ordem de Servico com Veículo nulo na validação")
    void deveRejeitarVeiculoIdNulo() {
        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.newOrdemServico(null, "Cliente relata barulho no motor"));

        assertEquals("Veículo ID não deve ser nulo", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve validar Ordem de Servico com Veículo valido com sucesso")
    void deveValidarOrdemServicoValida() {
        VeiculoID veiculoId = VeiculoID.unique();
        OrdemServico os = OrdemServico.newOrdemServico(veiculoId, "Cliente relata barulho no motor");

        assertEquals(veiculoId, os.getVeiculoId());
    }

    @Test
    @DisplayName("Deve rejeitar Ordem de Servico com relato vazio")
    void deveRejeitarRelatoVazio() {
        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.newOrdemServico(VeiculoID.unique(), " "));

        assertEquals("Relato do cliente não deve ser nulo ou vazio", exception.getErrors().getFirst().message());
    }
}
