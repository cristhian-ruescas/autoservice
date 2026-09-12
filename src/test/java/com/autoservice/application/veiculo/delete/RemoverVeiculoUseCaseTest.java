package com.autoservice.application.veiculo.delete;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
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
class RemoverVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @InjectMocks
    private RemoverVeiculoUseCase useCase;

    @Test
    void removeVeiculoExistente() {
        final var vid = VeiculoID.unique();
        final var v = Veiculo.with(
                vid,
                PessoaID.unique(),
                TipoVeiculoID.unique(),
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(1000));

        when(veiculoGateway.findById(vid)).thenReturn(Optional.of(v));

        useCase.execute(RemoverVeiculoCommand.with(UUID.fromString(vid.getValue())));

        verify(veiculoGateway).deleteById(vid);
    }

    @Test
    void veiculoInexistenteFalha() {
        final var vid = VeiculoID.unique();
        when(veiculoGateway.findById(vid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(RemoverVeiculoCommand.with(UUID.fromString(vid.getValue()))));
    }
}
