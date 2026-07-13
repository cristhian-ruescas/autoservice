package com.autoservice.application.ordemservico.diagnostico;

import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoDiagnosticoIniciadoEvent;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IniciarDiagnosticoUseCase")
class IniciarDiagnosticoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private IniciarDiagnosticoUseCase useCase;

    @Test
    @DisplayName("Deve iniciar diagnóstico da Ordem de Servico")
    void deveIniciarDiagnostico() {
        final var ordemServico = OrdemServico.newOrdemServico(
                VeiculoID.unique(),
                "Cliente relata barulho ao frear"
        );
        final var command = IniciarDiagnosticoCommand.with(UUID.fromString(ordemServico.getId().getValue()));

        when(gateway.findById(ordemServico.getId())).thenReturn(Optional.of(ordemServico));
        when(gateway.update(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var output = useCase.execute(command);

        assertEquals(ordemServico.getId().getValue(), output.id());
        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO.name(), output.status());

        verify(gateway).findById(ordemServico.getId());
        verify(gateway).update(ordemServico);
        verify(eventPublisher).publishEvent(any(OrdemServicoDiagnosticoIniciadoEvent.class));
    }

    @Test
    @DisplayName("Deve falhar quando Ordem de Servico não existir")
    void deveFalharQuandoOrdemServicoNaoExistir() {
        final var command = IniciarDiagnosticoCommand.with(UUID.randomUUID());

        when(gateway.findById(any())).thenReturn(Optional.empty());

        final var exception = assertThrows(DomainException.class, () -> useCase.execute(command));

        assertEquals("Ordem de serviço não encontrada", exception.getMessage());
    }
}
