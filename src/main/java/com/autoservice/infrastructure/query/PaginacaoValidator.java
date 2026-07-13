package com.autoservice.infrastructure.query;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;

public final class PaginacaoValidator {

    public static final int TAMANHO_MAXIMO_PAGINA = 100;

    private PaginacaoValidator() {
    }

    public static void validar(final int page, final int size) {
        if (page < 0) {
            throw DomainException.with(new Error("Página não deve ser menor que zero"));
        }
        if (size <= 0) {
            throw DomainException.with(new Error("Tamanho da página deve ser maior que zero"));
        }
        if (size > TAMANHO_MAXIMO_PAGINA) {
            throw DomainException.with(new Error("Tamanho da página não deve ser maior que 100"));
        }
    }
}
