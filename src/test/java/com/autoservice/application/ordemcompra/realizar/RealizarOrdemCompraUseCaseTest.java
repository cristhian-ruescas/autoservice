package com.autoservice.application.ordemcompra.realizar;

import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraGateway;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealizarOrdemCompraUseCaseTest {

    @Mock
    private OrdemCompraGateway ordemCompraGateway;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private RealizarOrdemCompraUseCase useCase;

    @Test
    void realizaOrdemCompraPendente() {
        final var id = OrdemCompraID.unique();
        final var oc = OrdemCompra.with(id, OrdemCompraStatus.PENDENTE, DataCompra.from(LocalDate.now()));

        when(ordemCompraGateway.findById(id)).thenReturn(Optional.of(oc));
        when(ordemCompraGateway.update(any(OrdemCompra.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(RealizarOrdemCompraCommand.with(UUID.fromString(id.getValue())));

        assertEquals(OrdemCompraStatus.REALIZADO.name(), out.status());
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void ordemCompraNaoEncontrada() {
        final var id = OrdemCompraID.unique();
        when(ordemCompraGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(RealizarOrdemCompraCommand.with(UUID.fromString(id.getValue()))));
    }
}
