package com.autoservice.application.ordemservico.diagnostico;

import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinalizarDiagnosticoUseCase")
class FinalizarDiagnosticoUseCaseTest {

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private ItemServicoGateway itemServicoGateway;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private FinalizarDiagnosticoUseCase useCase;

    @Test
    void ordemNaoEncontrada() {
        final OrdemServicoID id = OrdemServicoID.unique();
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.empty());
        final var cmd = FinalizarDiagnosticoCommand.with(UUID.fromString(id.getValue()), 1, 0);
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void valorTotalInvalido() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(itemServicoGateway.totalByOrdemServicoId(id)).thenReturn(BigDecimal.ZERO);
        final var cmd = FinalizarDiagnosticoCommand.with(UUID.fromString(id.getValue()), 1, 0);
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void finalizaComSucesso() {
        final OrdemServicoID id = OrdemServicoID.unique();
        final OrdemServico os = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_DIAGNOSTICO,
                DataCriacao.from(LocalDate.now()),
                "r");
        when(ordemServicoGateway.findById(id)).thenReturn(Optional.of(os));
        when(itemServicoGateway.totalByOrdemServicoId(id)).thenReturn(new BigDecimal("50.00"));
        when(ordemServicoGateway.update(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final OrdemServicoStatusOutput out = useCase.execute(
                FinalizarDiagnosticoCommand.with(UUID.fromString(id.getValue()), 2, 5));

        assertEquals(OrdemServicoStatus.AGUARDANDO_APROVACAO.name(), out.status());
        verify(eventPublisher).publishEvent(any());
    }
}
