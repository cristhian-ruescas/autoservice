package com.autoservice.application.estoque.localizacao;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarLocalizacaoEstoqueUseCaseTest {

    @Mock
    private EstoqueGateway estoqueGateway;

    @InjectMocks
    private AtualizarLocalizacaoEstoqueUseCase useCase;

    @Test
    void atualizaLocalizacao() {
        final var eid = EstoqueID.unique();
        final var estoque = Estoque.with(eid, 10, 2, "Gaveta A");

        when(estoqueGateway.findById(eid)).thenReturn(Optional.of(estoque));
        when(estoqueGateway.create(any(Estoque.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var out = useCase.execute(AtualizarLocalizacaoEstoqueCommand.with(
                UUID.fromString(eid.getValue()),
                "Prateleira B"));

        assertEquals("Prateleira B", out.localizacao());
    }

    @Test
    void estoqueNaoEncontrado() {
        final var eid = EstoqueID.unique();
        when(estoqueGateway.findById(eid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarLocalizacaoEstoqueCommand.with(
                UUID.fromString(eid.getValue()),
                "X")));
    }
}
