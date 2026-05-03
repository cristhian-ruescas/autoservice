package com.autoservice.application.ordemservico.create;

import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoCriadaEvent;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultCreateOrdemServicoUseCase")
class DefaultCreateOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoGateway gateway;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private DefaultCreateOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve criar Ordem de Servico com sucesso e disparar eventos")
    void deveCriarOrdemServicoComSucesso() {
        final UUID veiculoIdParam = UUID.randomUUID();
        final CreateOrdemServicoCommand command = CreateOrdemServicoCommand.with(
                veiculoIdParam,
                "Cliente relata falha ao dar partida"
        );

        when(gateway.create(any(OrdemServico.class))).thenAnswer(returnsFirstArg());

        final CreateOrdemServicoOutput output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(gateway, times(1)).create(argThat(os -> {
            assertEquals(VeiculoID.from(veiculoIdParam), os.getVeiculoId());
            assertEquals(OrdemServicoStatus.RECEBIDO, os.getStatus());
            assertEquals("Cliente relata falha ao dar partida", os.getRelato());
            assertNotNull(os.getId());
            assertNotNull(os.getDataCriacao());
            return true;
        }));

        verify(publisher, times(1)).publishEvent(any(OrdemServicoCriadaEvent.class));
    }
}
