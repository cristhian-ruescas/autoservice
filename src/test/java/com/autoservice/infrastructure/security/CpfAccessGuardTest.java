package com.autoservice.infrastructure.security;

import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.ordemservico.persistence.OrdemServicoRepository;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CpfAccessGuardTest {

    private final OrdemServicoRepository repository = mock(OrdemServicoRepository.class);
    private final CpfAccessGuard cpfAccessGuard = new CpfAccessGuard(repository);

    @Test
    void permiteClienteAcessarSuaOrdemServico() {
        final var id = UUID.randomUUID();
        final var authentication = new UsernamePasswordAuthenticationToken(
                "390.533.447-05", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        when(repository.findProprietarioCpfById(id.toString())).thenReturn(Optional.of(CPF.from("39053344705")));

        assertTrue(cpfAccessGuard.canAccessOrdemServico(id, authentication));
    }

    @Test
    void bloqueiaOrdemServicoDeOutroClienteOuSemProprietarioCpf() {
        final var id = UUID.randomUUID();
        final var authentication = new UsernamePasswordAuthenticationToken(
                "39053344705", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        when(repository.findProprietarioCpfById(id.toString()))
                .thenReturn(Optional.of(CPF.from("11144477735")))
                .thenReturn(Optional.empty());

        assertFalse(cpfAccessGuard.canAccessOrdemServico(id, authentication));
        assertFalse(cpfAccessGuard.canAccessOrdemServico(id, authentication));
    }

    @Test
    void permiteAdminAcessarOrdemServicoSemConsultarProprietario() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "admin@autoservice.local", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        assertTrue(cpfAccessGuard.canAccessOrdemServico(UUID.randomUUID(), authentication));
        verifyNoInteractions(repository);
    }

    @Test
    void bloqueiaOrdemServicoSemAutenticacaoPerfilOuId() {
        final var id = UUID.randomUUID();
        final var cliente = new UsernamePasswordAuthenticationToken(
                "39053344705", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        final var outroPerfil = new UsernamePasswordAuthenticationToken(
                "39053344705", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        assertFalse(cpfAccessGuard.canAccessOrdemServico(id, null));
        assertFalse(cpfAccessGuard.canAccessOrdemServico(id,
                new UsernamePasswordAuthenticationToken("39053344705", null)));
        assertFalse(cpfAccessGuard.canAccessOrdemServico(id, outroPerfil));
        assertFalse(cpfAccessGuard.canAccessOrdemServico(null, cliente));
        verifyNoInteractions(repository);
    }

    @Test
    void bloqueiaCpfVazioMesmoQuandoSubjectNaoContemDigitos() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "cliente", null, List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertFalse(cpfAccessGuard.canAccessCpf(null, authentication));
        assertFalse(cpfAccessGuard.canAccessCpf("", authentication));
    }

    @Test
    void permiteAdminConsultarQualquerCpf() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "admin@autoservice.local",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        assertTrue(cpfAccessGuard.canAccessCpf("390.533.447-05", authentication));
    }

    @Test
    void permiteClienteConsultarMesmoCpf() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "39053344705",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertTrue(cpfAccessGuard.canAccessCpf("390.533.447-05", authentication));
    }

    @Test
    void bloqueiaClienteConsultarCpfDiferente() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "39053344705",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        assertFalse(cpfAccessGuard.canAccessCpf("111.444.777-35", authentication));
    }

    @Test
    void bloqueiaAberturaDeAtendimentoQuandoCpfNaoBateComToken() {
        final var authentication = new UsernamePasswordAuthenticationToken(
                "39053344705",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        final var request = new AbrirAtendimentoRequest(
                "PF",
                "Cliente Teste",
                "111.444.777-35",
                null,
                null,
                null,
                null,
                null,
                null,
                "cliente@teste.com",
                "11999999999",
                "ABC1D23",
                "Fiat",
                "Uno",
                2020,
                "Prata",
                1000,
                "Ruido no motor",
                List.of()
        );

        assertFalse(cpfAccessGuard.canOpenAtendimento(request, authentication));
    }
}
