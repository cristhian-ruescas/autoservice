package com.autoservice.application.cliente.create;

import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.PessoaAtendimentoOrchestrator;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarClienteUseCase")
class CadastrarClienteUseCaseTest {

    private static final String CPF_VALIDO = "52998224725";

    @Mock
    private PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator;

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private CadastrarClienteUseCase useCase;

    @Test
    void cadastraPessoaFisicaComSucesso() {
        final var pessoaId = PessoaID.unique();
        final var pessoa = PessoaFisica.withId(
                pessoaId,
                Email.from("cliente@test.local"),
                Telefone.from("11987654321"),
                "Maria Cliente",
                CPF.from(CPF_VALIDO)
        );
        final var cliente = Cliente.with(ClienteID.unique(), pessoaId, LocalDate.now());

        when(this.pessoaAtendimentoOrchestrator.obterOuCriar(any(AbrirAtendimentoCommand.class)))
                .thenReturn(new PessoaAtendimentoOrchestrator.PessoaResultado(pessoa, true, null, false));
        when(this.clienteGateway.findByPessoaId(pessoaId)).thenReturn(Optional.empty());
        when(this.clienteGateway.create(any(Cliente.class))).thenReturn(cliente);

        final var output = this.useCase.execute(CadastrarClienteCommand.with(
                TipoPessoaAtendimento.FISICA,
                "Maria Cliente",
                CPF_VALIDO,
                null,
                null,
                null,
                null,
                null,
                null,
                "cliente@test.local",
                "11987654321"
        ));

        assertEquals(cliente.getId().getValue(), output.id());
        assertEquals("Maria Cliente", output.nome());
        verify(this.clienteGateway).create(any(Cliente.class));
    }

    @Test
    void falhaQuandoClienteJaExisteParaPessoa() {
        final var pessoaId = PessoaID.unique();
        final var pessoa = PessoaFisica.withId(
                pessoaId,
                Email.from("cliente@test.local"),
                Telefone.from("11987654321"),
                "Maria Cliente",
                CPF.from(CPF_VALIDO)
        );
        final var cliente = Cliente.with(ClienteID.unique(), pessoaId, LocalDate.now());

        when(this.pessoaAtendimentoOrchestrator.obterOuCriar(any(AbrirAtendimentoCommand.class)))
                .thenReturn(new PessoaAtendimentoOrchestrator.PessoaResultado(pessoa, false, null, false));
        when(this.clienteGateway.findByPessoaId(pessoaId)).thenReturn(Optional.of(cliente));

        assertThrows(DomainException.class, () -> this.useCase.execute(CadastrarClienteCommand.with(
                TipoPessoaAtendimento.FISICA,
                "Maria Cliente",
                CPF_VALIDO,
                null,
                null,
                null,
                null,
                null,
                null,
                "cliente@test.local",
                "11987654321"
        )));

        verify(this.clienteGateway, never()).create(any(Cliente.class));
    }
}
