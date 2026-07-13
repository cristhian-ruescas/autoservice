package com.autoservice.infrastructure.query;

import com.autoservice.application.pessoa.TipoPessoaCodigo;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;

public final class QueryFilterNormalizer {

    private QueryFilterNormalizer() {
    }

    public static String buscaParcial(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return "%" + value.trim().toLowerCase() + "%";
    }

    public static String tipoPessoa(final String tipoPessoa) {
        if (tipoPessoa == null || tipoPessoa.isBlank()) {
            return null;
        }

        final var normalizado = tipoPessoa.trim().toUpperCase();
        if (!TipoPessoaCodigo.FISICA.equals(normalizado) && !TipoPessoaCodigo.JURIDICA.equals(normalizado)) {
            throw DomainException.with(new Error("Tipo de pessoa deve ser FISICA ou JURIDICA"));
        }

        return normalizado;
    }
}
