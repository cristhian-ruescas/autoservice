package com.autoservice.application.cliente.update;

import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.pessoa.RepresentanteLegalOrchestrator;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarClienteUseCase")
class AtualizarClienteUseCaseTest {

    private static final String CPF_VALIDO = "52998224725";
    private static final String CNPJ_VALIDO = "11222333000181";

    @Mock
    private ClienteGateway clienteGateway;

    @Mock
    private PessoaGateway pessoaGateway;

    @Mock
    private RepresentanteLegalOrchestrator representanteLegalOrchestrator;

    @InjectMocks
    private AtualizarClienteUseCase useCase;

    @Test
    void comandoNuloFalha() {
        assertThrows(DomainException.class, () -> useCase.execute(null));
    }

    @Test
    void clienteIdNuloFalha() {
        final var cmd = AtualizarClienteCommand.with(
                null, "Nome", null, "a@b.com", "11999999999", null, null, null, null);
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void clienteNaoEncontrado() {
        final var id = UUID.randomUUID();
        when(clienteGateway.findById(ClienteID.from(id))).thenReturn(Optional.empty());
        final var cmd = AtualizarClienteCommand.with(
                id, "Nome", null, "a@b.com", "11999999999", null, null, null, null);
        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void atualizaPessoaFisicaComSucesso() {
        final var clienteId = ClienteID.unique();
        final var pessoaId = PessoaID.unique();
        final var cliente = Cliente.with(clienteId, pessoaId, LocalDate.now());
        final var pf = PessoaFisica.withId(
                pessoaId,
                Email.from("velho@email.com"),
                Telefone.from("11888888888"),
                "Nome Antigo",
                CPF.from(CPF_VALIDO)
        );

        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pessoaGateway.findById(pessoaId)).thenReturn(Optional.of(pf));
        when(pessoaGateway.update(any(PessoaFisica.class))).thenAnswer(returnsFirstArg());

        final var cmd = AtualizarClienteCommand.with(
                UUID.fromString(clienteId.getValue()),
                "Nome Novo",
                null,
                "novo@email.com",
                "11777777777",
                null,
                null,
                null,
                null);

        final ClienteOutput out = useCase.execute(cmd);

        assertEquals("FISICA", out.tipoPessoa());
        assertEquals("Nome Novo", out.nome());
        assertEquals("novo@email.com", out.email());
        verify(pessoaGateway).update(any(PessoaFisica.class));
    }

    @Test
    void pessoaFisicaNomeEmBrancoFalha() {
        final var clienteId = ClienteID.unique();
        final var pessoaId = PessoaID.unique();
        final var cliente = Cliente.with(clienteId, pessoaId, LocalDate.now());
        final var pf = PessoaFisica.withId(
                pessoaId,
                Email.from("a@b.com"),
                Telefone.from("11999999999"),
                "Nome",
                CPF.from(CPF_VALIDO)
        );

        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pessoaGateway.findById(pessoaId)).thenReturn(Optional.of(pf));

        final var cmd = AtualizarClienteCommand.with(
                UUID.fromString(clienteId.getValue()),
                "   ",
                null,
                "a@b.com",
                "11999999999",
                null,
                null,
                null,
                null);

        assertThrows(DomainException.class, () -> useCase.execute(cmd));
    }

    @Test
    void atualizaPessoaJuridicaMantendoRepresentante() {
        final var clienteId = ClienteID.unique();
        final var pessoaId = PessoaID.unique();
        final var representanteId = PessoaID.unique();
        final var cliente = Cliente.with(clienteId, pessoaId, LocalDate.now());
        final var pj = PessoaJuridica.withId(
                pessoaId,
                Email.from("empresa@email.com"),
                Telefone.from("11999999999"),
                "Razao Antiga",
                CNPJ.from(CNPJ_VALIDO),
                representanteId
        );
        final var representante = PessoaFisica.withId(
                representanteId,
                Email.from("rep@email.com"),
                Telefone.from("11555555555"),
                "Rep Nome",
                CPF.from(CPF_VALIDO)
        );

        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pessoaGateway.findById(pessoaId)).thenReturn(Optional.of(pj));
        when(pessoaGateway.findById(representanteId)).thenReturn(Optional.of(representante));
        when(pessoaGateway.update(any(PessoaJuridica.class))).thenAnswer(returnsFirstArg());

        final var cmd = AtualizarClienteCommand.with(
                UUID.fromString(clienteId.getValue()),
                null,
                "Razao Nova",
                "nova@empresa.com",
                "11666666666",
                null,
                null,
                null,
                null);

        final ClienteOutput out = useCase.execute(cmd);

        assertEquals("JURIDICA", out.tipoPessoa());
        assertEquals("Razao Nova", out.razaoSocial());
        verify(pessoaGateway).update(any(PessoaJuridica.class));
    }

    @Test
    void atualizaRepresentanteLegalCriandoNovaPessoaFisica() {
        final var clienteId = ClienteID.unique();
        final var pessoaId = PessoaID.unique();
        final var cliente = Cliente.with(clienteId, pessoaId, LocalDate.now());
        final var pj = PessoaJuridica.withId(
                pessoaId,
                Email.from("empresa@email.com"),
                Telefone.from("11999999999"),
                "Razao",
                CNPJ.from(CNPJ_VALIDO),
                null
        );

        when(clienteGateway.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pessoaGateway.findById(pessoaId)).thenReturn(Optional.of(pj));
        final var representante = PessoaFisica.withId(
                PessoaID.unique(),
                Email.from("rep@email.com"),
                Telefone.from("11555555555"),
                "Rep Novo",
                CPF.from(CPF_VALIDO)
        );
        when(representanteLegalOrchestrator.obterOuCriar(
                "Rep Novo",
                CPF_VALIDO,
                "rep@email.com",
                "11555555555"
        )).thenReturn(new RepresentanteLegalOrchestrator.Resultado(representante, true));
        when(pessoaGateway.update(any(PessoaJuridica.class))).thenAnswer(returnsFirstArg());

        final var cmd = AtualizarClienteCommand.with(
                UUID.fromString(clienteId.getValue()),
                null,
                "Razao",
                "empresa@email.com",
                "11999999999",
                "Rep Novo",
                CPF_VALIDO,
                "rep@email.com",
                "11555555555");

        useCase.execute(cmd);

        verify(representanteLegalOrchestrator).obterOuCriar(
                "Rep Novo",
                CPF_VALIDO,
                "rep@email.com",
                "11555555555"
        );
        verify(pessoaGateway).update(any(PessoaJuridica.class));
    }
}
