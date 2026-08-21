package com.autoservice.application.servico.delete;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.servico.ServicoID;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RemoverServicoUseCase")
class RemoverServicoUseCaseTest {

    @Mock
    private ServicoGateway servicoGateway;

    @InjectMocks
    private RemoverServicoUseCase useCase;

    @Test
    void removeQuandoServicoExiste() {
        final var id = ServicoID.unique();
        when(servicoGateway.findById(id)).thenReturn(Optional.ofNullable(org.mockito.Mockito.mock(com.autoservice.domain.servico.Servico.class)));

        useCase.execute(RemoverServicoCommand.with(UUID.fromString(id.getValue())));

        verify(servicoGateway).deleteById(id);
    }

    @Test
    void falhaQuandoComandoOuIdNulos() {
        final var ex1 = assertThrows(DomainException.class, () -> useCase.execute(null));
        assertEquals("Serviço é obrigatório para remoção", ex1.getMessage());

        final var ex2 = assertThrows(DomainException.class, () -> useCase.execute(RemoverServicoCommand.with(null)));
        assertEquals("Serviço é obrigatório para remoção", ex2.getMessage());
    }

    @Test
    void falhaQuandoServicoNaoExiste() {
        final var id = ServicoID.unique();
        when(servicoGateway.findById(id)).thenReturn(Optional.empty());

        final var exception = assertThrows(
                DomainException.class,
                () -> useCase.execute(RemoverServicoCommand.with(UUID.fromString(id.getValue())))
        );

        assertEquals("Serviço não encontrado", exception.getMessage());
    }
}
