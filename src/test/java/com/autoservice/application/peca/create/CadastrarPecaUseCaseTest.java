package com.autoservice.application.peca.create;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
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
class CadastrarPecaUseCaseTest {

    @Mock
    private PecaGateway pecaGateway;

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private CadastrarPecaUseCase useCase;

    @Test
    void cadastraSemTipoVeiculo() {
        when(pecaGateway.create(any(Peca.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(CadastrarPecaCommand.with(
                "Filtro",
                "F-1",
                "Mann",
                new BigDecimal("25.00"),
                null));

        assertEquals("Filtro", out.descricao());
    }

    @Test
    void cadastraComTipoVeiculoValido() {
        final var tid = TipoVeiculoID.unique();
        final var tipo = TipoVeiculo.with(
                tid,
                Marca.from("Fiat"),
                Modelo.from("Uno"),
                Ano.from(2015));

        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.of(tipo));
        when(pecaGateway.create(any(Peca.class))).thenAnswer(returnsFirstArg());

        final var out = useCase.execute(CadastrarPecaCommand.with(
                "Pastilha",
                "P-9",
                "Bosch",
                new BigDecimal("40.00"),
                UUID.fromString(tid.getValue())));

        assertEquals("Pastilha", out.descricao());
    }

    @Test
    void tipoVeiculoInexistenteFalha() {
        final var tid = TipoVeiculoID.unique();
        when(tipoVeiculoGateway.findById(tid)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> useCase.execute(CadastrarPecaCommand.with(
                "x",
                "c",
                "m",
                BigDecimal.ONE,
                UUID.fromString(tid.getValue()))));
    }
}
