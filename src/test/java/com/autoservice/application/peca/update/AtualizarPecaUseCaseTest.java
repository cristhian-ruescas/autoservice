package com.autoservice.application.peca.update;

import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarPecaUseCaseTest {

    @Mock
    private PecaGateway pecaGateway;

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private AtualizarPecaUseCase useCase;

    @Test
    void atualizaDescricao() {
        final var id = PecaID.unique();
        final var tipoAtual = TipoVeiculoID.unique();
        final var peca = Peca.with(
                id,
                "Velha",
                "C1",
                "M",
                new BigDecimal("10.00"),
                EstoqueID.unique(),
                tipoAtual);

        when(pecaGateway.findById(id)).thenReturn(Optional.of(peca));
        when(pecaGateway.update(any(Peca.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarPecaCommand.with(
                UUID.fromString(id.getValue()),
                "Nova desc",
                null,
                null,
                null,
                null));

        assertEquals("Nova desc", out.descricao());
    }

    @Test
    void atualizaTipoVeiculo() {
        final var id = PecaID.unique();
        final var tipoAtual = TipoVeiculoID.unique();
        final var tipoNovo = TipoVeiculoID.unique();
        final var peca = Peca.with(
                id,
                "Peca",
                "C1",
                "M",
                new BigDecimal("10.00"),
                EstoqueID.unique(),
                tipoAtual);
        final var tipo = TipoVeiculo.with(
                tipoNovo,
                Marca.from("VW"),
                Modelo.from("Gol"),
                Ano.from(2018));

        when(pecaGateway.findById(id)).thenReturn(Optional.of(peca));
        when(tipoVeiculoGateway.findById(tipoNovo)).thenReturn(Optional.of(tipo));
        when(pecaGateway.update(any(Peca.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarPecaCommand.with(
                UUID.fromString(id.getValue()),
                null,
                null,
                null,
                null,
                UUID.fromString(tipoNovo.getValue())));

        assertEquals(tipoNovo.getValue(), out.tipoVeiculoId());
    }

    @Test
    void pecaNaoEncontrada() {
        final var id = PecaID.unique();
        when(pecaGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarPecaCommand.with(
                UUID.fromString(id.getValue()),
                "x",
                null,
                null,
                null,
                null)));
    }
}
