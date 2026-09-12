package com.autoservice.application.pessoa;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepresentanteLegalOrchestrator")
class RepresentanteLegalOrchestratorTest {

    @Mock
    private PessoaGateway pessoaGateway;

    @InjectMocks
    private RepresentanteLegalOrchestrator orchestrator;

    @Test
    void reutilizaRepresentanteQuandoCpfExiste() {
        final var existente = PessoaFisica.newPessoaFisica(
                Email.from("maria@empresa.com"),
                Telefone.from("11988887777"),
                "Maria",
                CPF.from("52998224725")
        );
        when(pessoaGateway.findPessoaFisicaByCpf(CPF.from("52998224725"))).thenReturn(Optional.of(existente));

        final var resultado = orchestrator.obterOuCriar("Maria", "52998224725", "maria@empresa.com", "11988887777");

        assertSame(existente, resultado.pessoa());
        assertFalse(resultado.criada());
    }

    @Test
    void criaRepresentanteQuandoCpfNaoExiste() {
        when(pessoaGateway.findPessoaFisicaByCpf(CPF.from("52998224725"))).thenReturn(Optional.empty());
        when(pessoaGateway.create(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var resultado = orchestrator.obterOuCriar("Maria", "52998224725", "maria@empresa.com", "11988887777");

        assertTrue(resultado.criada());
        verify(pessoaGateway, times(1)).create(any(Pessoa.class));
    }

    @Test
    void atualizaRepresentanteExistente() {
        final var existente = PessoaFisica.newPessoaFisica(
                Email.from("old@empresa.com"),
                Telefone.from("11977776666"),
                "Nome Antigo",
                CPF.from("52998224725")
        );
        when(pessoaGateway.update(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var atualizado = orchestrator.atualizar(
                existente,
                "Nome Novo",
                "52998224725",
                "novo@empresa.com",
                "11999999999"
        );

        assertEquals(existente.getId(), atualizado.getId());
        assertEquals("Nome Novo", atualizado.getNome());
        assertEquals("novo@empresa.com", atualizado.getEmail().getValue());
        assertEquals("11999999999", atualizado.getTelefone().getValue());
    }

    @Test
    void falhaQuandoNomeNaoInformado() {
        final var exception = assertThrows(
                DomainException.class,
                () -> orchestrator.obterOuCriar(" ", "52998224725", "maria@empresa.com", "11988887777")
        );
        assertEquals("Nome do representante legal é obrigatório", exception.getMessage());
    }
}
