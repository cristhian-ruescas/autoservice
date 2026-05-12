package com.autoservice.application.atendimento.create;

import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.OrdemServicoCriadaEvent;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbrirAtendimentoUseCase")
class AbrirAtendimentoUseCaseTest {

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private VeiculoGateway veiculoGateway;

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @Mock
    private OrdemServicoGateway ordemServicoGateway;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private AbrirAtendimentoUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar pessoa, cliente, veiculo e criar ordem de servico")
    void deveAbrirAtendimentoComSucesso() {
        final var command = AbrirAtendimentoCommand.with(
                TipoPessoaAtendimento.FISICA,
                "Joao Silva",
                "52998224725",
                null,
                null,
                null,
                null,
                null,
                null,
                "joao@email.com",
                "11999999999",
                "ABC1D23",
                "Toyota",
                "Corolla",
                2023,
                "Preto",
                10000,
                "Cliente relata barulho ao frear"
        );

        when(pessoaGateway.findPessoaFisicaByCpf(eq(CPF.from("52998224725"))))
                .thenReturn(Optional.empty());

        when(pessoaGateway.create(any(Pessoa.class)))
                .thenAnswer(returnsFirstArg());

        when(clienteGateway.create(any(Cliente.class)))
                .thenAnswer(returnsFirstArg());

        when(tipoVeiculoGateway.findByMarcaModeloAno(eq("Toyota"), eq("Corolla"), eq(2023)))
                .thenReturn(Optional.empty());

        when(tipoVeiculoGateway.create(any(TipoVeiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(veiculoGateway.create(any(Veiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(ordemServicoGateway.create(any(OrdemServico.class)))
                .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.clienteId());
        assertNotNull(output.veiculoId());
        assertNotNull(output.ordemServicoId());
        assertEquals(OrdemServicoStatus.RECEBIDO.name(), output.status());

        final var pessoaCaptor = ArgumentCaptor.forClass(Pessoa.class);
        final var clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        final var veiculoCaptor = ArgumentCaptor.forClass(Veiculo.class);
        final var ordemCaptor = ArgumentCaptor.forClass(OrdemServico.class);

        verify(pessoaGateway, times(1)).create(pessoaCaptor.capture());
        verify(clienteGateway, times(1)).create(clienteCaptor.capture());
        verify(tipoVeiculoGateway, times(1)).create(any(TipoVeiculo.class));
        verify(veiculoGateway, times(1)).create(veiculoCaptor.capture());
        verify(ordemServicoGateway, times(1)).create(ordemCaptor.capture());
        verify(eventPublisher, times(1)).publishEvent(any(OrdemServicoCriadaEvent.class));

        final var pessoa = (PessoaFisica) pessoaCaptor.getValue();

        assertEquals("Joao Silva", pessoa.getNome());
        assertEquals(pessoa.getId(), clienteCaptor.getValue().getPessoaId());
        assertEquals(pessoa.getId(), veiculoCaptor.getValue().getProprietarioId());
        assertEquals(veiculoCaptor.getValue().getId(), ordemCaptor.getValue().getVeiculoId());
        assertEquals("Cliente relata barulho ao frear", ordemCaptor.getValue().getRelato());
    }

    @Test
    @DisplayName("Deve cadastrar pessoa jurídica como cliente no atendimento")
    void deveAbrirAtendimentoParaPessoaJuridica() {
        final var command = AbrirAtendimentoCommand.with(
                TipoPessoaAtendimento.JURIDICA,
                null,
                null,
                "Empresa Teste Ltda",
                "11222333000181",
                "Maria Representante",
                "52998224725",
                "maria@empresa.com",
                "11988887777",
                "contato@empresa.com",
                "11999999999",
                "ABC1D23",
                "Toyota",
                "Corolla",
                2023,
                "Preto",
                10000,
                "Veículo da empresa apresenta falha na partida"
        );

        when(pessoaGateway.findPessoaJuridicaByCnpj(eq(CNPJ.from("11222333000181"))))
                .thenReturn(Optional.empty());

        when(pessoaGateway.findPessoaFisicaByCpf(eq(CPF.from("52998224725"))))
                .thenReturn(Optional.empty());

        when(pessoaGateway.create(any(Pessoa.class)))
                .thenAnswer(returnsFirstArg());

        when(clienteGateway.create(any(Cliente.class)))
                .thenAnswer(returnsFirstArg());

        when(tipoVeiculoGateway.findByMarcaModeloAno(eq("Toyota"), eq("Corolla"), eq(2023)))
                .thenReturn(Optional.empty());

        when(tipoVeiculoGateway.create(any(TipoVeiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(veiculoGateway.create(any(Veiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(ordemServicoGateway.create(any(OrdemServico.class)))
                .thenAnswer(returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(OrdemServicoStatus.RECEBIDO.name(), output.status());

        final var pessoaCaptor = ArgumentCaptor.forClass(Pessoa.class);
        final var clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
        final var veiculoCaptor = ArgumentCaptor.forClass(Veiculo.class);

        verify(pessoaGateway, times(2)).create(pessoaCaptor.capture());
        verify(clienteGateway, times(1)).create(clienteCaptor.capture());
        verify(tipoVeiculoGateway, times(1)).create(any(TipoVeiculo.class));
        verify(veiculoGateway, times(1)).create(veiculoCaptor.capture());
        verify(ordemServicoGateway, times(1)).create(any(OrdemServico.class));
        verify(eventPublisher, times(1)).publishEvent(any(OrdemServicoCriadaEvent.class));

        final var representante = (PessoaFisica) pessoaCaptor.getAllValues().get(0);
        final var pessoa = (PessoaJuridica) pessoaCaptor.getAllValues().get(1);

        assertEquals("Maria Representante", representante.getNome());
        assertEquals("52998224725", representante.getCpf().getValue());
        assertEquals("Empresa Teste Ltda", pessoa.getRazaoSocial());
        assertEquals("11222333000181", pessoa.getCnpj().getValue());
        assertEquals(representante.getId(), pessoa.getRepresentanteLegalId());
        assertEquals(pessoa.getId(), clienteCaptor.getValue().getPessoaId());
        assertEquals(pessoa.getId(), veiculoCaptor.getValue().getProprietarioId());
    }

    @Test
    @DisplayName("Deve reutilizar representante legal existente por CPF")
    void deveReutilizarRepresentanteLegalExistente() {
        final var representanteExistente = PessoaFisica.newPessoaFisica(
                Email.from("maria@empresa.com"),
                Telefone.from("11988887777"),
                "Maria Representante",
                CPF.from("52998224725")
        );

        final var command = AbrirAtendimentoCommand.with(
                TipoPessoaAtendimento.JURIDICA,
                null,
                null,
                "Empresa Teste Ltda",
                "11222333000181",
                "Maria Representante",
                "52998224725",
                "maria@empresa.com",
                "11988887777",
                "contato@empresa.com",
                "11999999999",
                "ABC1D23",
                "Toyota",
                "Corolla",
                2023,
                "Preto",
                10000,
                "Veículo da empresa apresenta falha na partida"
        );

        when(pessoaGateway.findPessoaJuridicaByCnpj(eq(CNPJ.from("11222333000181"))))
                .thenReturn(Optional.empty());

        when(pessoaGateway.findPessoaFisicaByCpf(eq(CPF.from("52998224725"))))
                .thenReturn(Optional.of(representanteExistente));

        when(pessoaGateway.create(any(Pessoa.class)))
                .thenAnswer(returnsFirstArg());

        when(clienteGateway.create(any(Cliente.class)))
                .thenAnswer(returnsFirstArg());

        when(tipoVeiculoGateway.findByMarcaModeloAno(eq("Toyota"), eq("Corolla"), eq(2023)))
                .thenReturn(Optional.empty());

        when(tipoVeiculoGateway.create(any(TipoVeiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(veiculoGateway.create(any(Veiculo.class)))
                .thenAnswer(returnsFirstArg());

        when(ordemServicoGateway.create(any(OrdemServico.class)))
                .thenAnswer(returnsFirstArg());

        useCase.execute(command);

        final var pessoaCaptor = ArgumentCaptor.forClass(Pessoa.class);

        verify(pessoaGateway, times(1)).create(pessoaCaptor.capture());
        verify(tipoVeiculoGateway, times(1)).create(any(TipoVeiculo.class));
        verify(eventPublisher, times(1)).publishEvent(any(OrdemServicoCriadaEvent.class));

        final var pessoaJuridica = (PessoaJuridica) pessoaCaptor.getValue();

        assertEquals(representanteExistente.getId(), pessoaJuridica.getRepresentanteLegalId());
    }
}