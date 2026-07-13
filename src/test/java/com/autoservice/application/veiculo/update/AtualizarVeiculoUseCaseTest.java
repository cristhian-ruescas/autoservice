package com.autoservice.application.veiculo.update;

import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    private AtualizarVeiculoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarVeiculoUseCase(
                veiculoGateway,
                new TipoVeiculoResolver(tipoVeiculoGateway)
        );
    }

    @Test
    void atualizaVeiculoReutilizandoTipoCadastrado() {
        final var vid = VeiculoID.unique();
        final var tid = TipoVeiculoID.unique();
        final var existente = Veiculo.with(
                vid,
                PessoaID.unique(),
                tid,
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(5000));
        final var tipo = TipoVeiculo.with(
                tid,
                Marca.from("Toyota"),
                Modelo.from("Corolla"),
                Ano.from(2020));

        when(veiculoGateway.findById(vid)).thenReturn(Optional.of(existente));
        when(tipoVeiculoGateway.findByMarcaModeloAno(eq("Toyota"), eq("Corolla"), eq(2020)))
                .thenReturn(Optional.of(tipo));
        when(veiculoGateway.update(any(Veiculo.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(AtualizarVeiculoCommand.with(
                UUID.fromString(vid.getValue()),
                "ABC1D23",
                "Toyota",
                "Corolla",
                2020,
                "Branco",
                12000));

        assertEquals("Toyota", out.marca());
        assertEquals(12000, out.kilometragem());
    }

    @Test
    void veiculoNaoEncontrado() {
        final var vid = VeiculoID.unique();
        when(veiculoGateway.findById(vid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(AtualizarVeiculoCommand.with(
                UUID.fromString(vid.getValue()),
                "ABC1D23",
                "Fiat",
                "Uno",
                2015,
                "Prata",
                10000)));
    }
}
