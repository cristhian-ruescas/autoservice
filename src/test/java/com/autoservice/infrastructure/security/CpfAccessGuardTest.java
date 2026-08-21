package com.autoservice.infrastructure.security;

import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfAccessGuardTest {

    private final CpfAccessGuard cpfAccessGuard = new CpfAccessGuard();

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
