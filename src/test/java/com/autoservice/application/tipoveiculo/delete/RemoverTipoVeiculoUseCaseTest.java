package com.autoservice.application.tipoveiculo.delete;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverTipoVeiculoUseCaseTest {

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private RemoverTipoVeiculoUseCase useCase;

    @Test
    void removeExistente() {
        final var tid = TipoVeiculoID.unique();
        final var tv = TipoVeiculo.with(tid, Marca.from("VW"), Modelo.from("Gol"), Ano.from(2018));

        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.of(tv));

        useCase.execute(RemoverTipoVeiculoCommand.with(UUID.fromString(tid.getValue())));

        verify(tipoVeiculoGateway).deleteById(tid);
    }

    @Test
    void naoEncontradoFalha() {
        final var tid = TipoVeiculoID.unique();
        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(RemoverTipoVeiculoCommand.with(UUID.fromString(tid.getValue()))));
    }
}
