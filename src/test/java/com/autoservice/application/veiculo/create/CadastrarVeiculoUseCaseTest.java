package com.autoservice.application.veiculo.create;

import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.domain.veiculo.valueobject.Placa;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarVeiculoUseCase")
class CadastrarVeiculoUseCaseTest {

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private TipoVeiculoResolver tipoVeiculoResolver;

    @InjectMocks
    private CadastrarVeiculoUseCase useCase;

    @Test
    void cadastraVeiculoComSucesso() {
        final var clienteId = UUID.randomUUID();
        final var pessoaId = PessoaID.unique();
        final var tipoVeiculoId = TipoVeiculoID.unique();
        final var cliente = Cliente.with(ClienteID.from(clienteId), pessoaId, LocalDate.now());
        final var tipoVeiculo = TipoVeiculo.with(
                tipoVeiculoId,
                Marca.from("VW"),
                Modelo.from("Gol"),
                Ano.from(2018)
        );
        final var veiculo = Veiculo.with(
                VeiculoID.unique(),
                pessoaId,
                tipoVeiculoId,
                Placa.from("ABC1D23"),
                Cor.from("Prata"),
                Kilometragem.from(42000)
        );

        when(this.clienteGateway.findById(ClienteID.from(clienteId))).thenReturn(Optional.of(cliente));
        when(this.tipoVeiculoResolver.obterOuCriar("VW", "Gol", 2018)).thenReturn(tipoVeiculo);
        when(this.veiculoGateway.create(any(Veiculo.class))).thenReturn(veiculo);

        final var output = this.useCase.execute(CadastrarVeiculoCommand.with(
                clienteId,
                "ABC1D23",
                "VW",
                "Gol",
                2018,
                "Prata",
                42000
        ));

        assertEquals(veiculo.getId().getValue(), output.id());
        assertEquals("ABC1D23", output.placa());
        verify(this.veiculoGateway).create(any(Veiculo.class));
    }

    @Test
    void falhaQuandoClienteNaoExiste() {
        final var clienteId = UUID.randomUUID();
        when(this.clienteGateway.findById(ClienteID.from(clienteId))).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> this.useCase.execute(CadastrarVeiculoCommand.with(
                clienteId,
                "ABC1D23",
                "VW",
                "Gol",
                2018,
                "Prata",
                42000
        )));
    }
}
