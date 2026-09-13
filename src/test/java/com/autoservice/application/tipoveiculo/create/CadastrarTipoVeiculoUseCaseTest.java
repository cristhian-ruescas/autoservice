package com.autoservice.application.tipoveiculo.create;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarTipoVeiculoUseCase")
class CadastrarTipoVeiculoUseCaseTest {

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private CadastrarTipoVeiculoUseCase useCase;

    @Test
    @DisplayName("Quando não existe tipo igual, cria e persiste via gateway")
    void criaQuandoNaoExiste() {
        final var command = CadastrarTipoVeiculoCommand.with("Ford", "Ka", 2019);
        when(tipoVeiculoGateway.findByMarcaModeloAno("Ford", "Ka", 2019)).thenReturn(Optional.empty());
        when(tipoVeiculoGateway.create(any(TipoVeiculo.class))).thenAnswer(returnsFirstArg());

        final CadastrarTipoVeiculoOutput output = useCase.execute(command);

        assertEquals("Ford", output.marca());
        assertEquals("Ka", output.modelo());
        assertEquals(2019, output.ano());
        verify(tipoVeiculoGateway).create(any(TipoVeiculo.class));
    }

    @Test
    @DisplayName("Quando já existe tipo com mesma marca, modelo e ano, não chama create")
    void reutilizaQuandoJaExiste() {
        final var existente = TipoVeiculo.with(
                TipoVeiculoID.from("id-fixo"),
                Marca.from("Ford"),
                Modelo.from("Ka"),
                Ano.from(2019));
        final var command = CadastrarTipoVeiculoCommand.with("Ford", "Ka", 2019);
        when(tipoVeiculoGateway.findByMarcaModeloAno("Ford", "Ka", 2019)).thenReturn(Optional.of(existente));

        final CadastrarTipoVeiculoOutput output = useCase.execute(command);

        assertEquals("id-fixo", output.id());
        verify(tipoVeiculoGateway, never()).create(any());
    }
}
