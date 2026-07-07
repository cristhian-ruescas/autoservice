package com.autoservice.application.tipoveiculo.update;

import com.autoservice.domain.exceptions.DomainException;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarTipoVeiculoUseCaseTest {

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private AtualizarTipoVeiculoUseCase useCase;

    @Test
    void atualizaTipoVeiculo() {
        final var tid = TipoVeiculoID.unique();
        final var atual = TipoVeiculo.with(
                tid,
                Marca.from("Fiat"),
                Modelo.from("Uno"),
                Ano.from(2010));

        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.of(atual));
        when(tipoVeiculoGateway.create(any(TipoVeiculo.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarTipoVeiculoCommand.with(
                UUID.fromString(tid.getValue()),
                "Ford",
                "Ka",
                2019));

        assertEquals("Ford", out.marca());
        assertEquals(2019, out.ano());
    }

    @Test
    void tipoNaoEncontrado() {
        final var tid = TipoVeiculoID.unique();
        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarTipoVeiculoCommand.with(
                UUID.fromString(tid.getValue()),
                "X",
                "Y",
                2020)));
    }
}
