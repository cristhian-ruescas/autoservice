package com.autoservice.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityAuth {

    private SecurityAuth() {
    }

    public static Optional<String> currentClientCpf() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (!hasRole(authentication, "ROLE_" + SecurityPaths.ROLE_CLIENTE)) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName())
                .map(SecurityAuth::digitsOnly)
                .filter(cpf -> !cpf.isBlank());
    }

    public static boolean isAdmin(final Authentication authentication) {
        return authentication != null && hasRole(authentication, "ROLE_" + SecurityPaths.ROLE_ADMIN);
    }

    public static String digitsOnly(final String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private static boolean hasRole(final Authentication authentication, final String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
