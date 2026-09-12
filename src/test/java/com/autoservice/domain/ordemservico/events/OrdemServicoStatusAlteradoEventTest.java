package com.autoservice.domain.ordemservico.events;

import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("OrdemServicoStatusAlteradoEvent")
class OrdemServicoStatusAlteradoEventTest {

    @Test
    void deveRegistrarEventoAoIniciarDiagnostico() {
        final var ordemServico = OrdemServico.with(
                OrdemServicoID.unique(),
                VeiculoID.unique(),
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                "relato"
        );

        ordemServico.iniciarDiagnostico();

        assertTrue(ordemServico.getDomainEvents().stream()
                .anyMatch(OrdemServicoStatusAlteradoEvent.class::isInstance));

        final var evento = ordemServico.getDomainEvents().stream()
                .filter(OrdemServicoStatusAlteradoEvent.class::isInstance)
                .map(OrdemServicoStatusAlteradoEvent.class::cast)
                .findFirst()
                .orElseThrow();

        assertEquals(OrdemServicoStatus.RECEBIDO, evento.getStatusAnterior());
        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, evento.getStatusNovo());
    }
}
