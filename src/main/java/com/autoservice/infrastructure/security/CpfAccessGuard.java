package com.autoservice.infrastructure.security;

import com.autoservice.infrastructure.ordemservico.persistence.OrdemServicoRepository;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component("cpfAccessGuard")
public class CpfAccessGuard {

    private final OrdemServicoRepository ordemServicoRepository;

    public CpfAccessGuard(final OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = Objects.requireNonNull(ordemServicoRepository);
    }

    public boolean canAccessCpf(final String cpf, final Authentication authentication) {
        if (isUnauthenticated(authentication)) {
            return false;
        }

        if (hasRole(authentication, SecurityPaths.ROLE_ADMIN)) {
            return true;
        }

        final String normalizedCpf = normalizeDigits(cpf);
        return hasRole(authentication, SecurityPaths.ROLE_CUSTOMER)
                && !normalizedCpf.isEmpty()
                && normalizedCpf.equals(normalizeDigits(authentication.getName()));
    }

    public boolean canOpenAtendimento(final AbrirAtendimentoRequest request, final Authentication authentication) {
        if (isUnauthenticated(authentication)) {
            return false;
        }

        if (hasRole(authentication, SecurityPaths.ROLE_ADMIN)) {
            return true;
        }

        return request != null && canAccessCpf(request.cpf(), authentication);
    }

    public boolean canAccessOrdemServico(final UUID id, final Authentication authentication) {
        if (isUnauthenticated(authentication)) {
            return false;
        }

        if (hasRole(authentication, SecurityPaths.ROLE_ADMIN)) {
            return true;
        }

        if (id == null || !hasRole(authentication, SecurityPaths.ROLE_CUSTOMER)) {
            return false;
        }

        return ordemServicoRepository.findProprietarioCpfById(id.toString())
                .map(cpf -> canAccessCpf(cpf.getValue(), authentication))
                .orElse(false);
    }

    private boolean isUnauthenticated(final Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated();
    }

    private boolean hasRole(final Authentication authentication, final String role) {
        final String expectedAuthority = "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(expectedAuthority::equals);
    }

    private String normalizeDigits(final String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^\\d]", "");
    }
}
