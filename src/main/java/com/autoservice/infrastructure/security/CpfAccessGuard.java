package com.autoservice.infrastructure.security;

import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("cpfAccessGuard")
public class CpfAccessGuard {

    public boolean canAccessCpf(final String cpf, final Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }

        if (hasRole(authentication, SecurityPaths.ROLE_ADMIN)) {
            return true;
        }

        return hasRole(authentication, SecurityPaths.ROLE_CUSTOMER)
                && normalizeDigits(cpf).equals(normalizeDigits(authentication.getName()));
    }

    public boolean canOpenAtendimento(final AbrirAtendimentoRequest request, final Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }

        if (hasRole(authentication, SecurityPaths.ROLE_ADMIN)) {
            return true;
        }

        return request != null
                && hasRole(authentication, SecurityPaths.ROLE_CUSTOMER)
                && normalizeDigits(request.cpf()).equals(normalizeDigits(authentication.getName()));
    }

    private boolean isAuthenticated(final Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
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
