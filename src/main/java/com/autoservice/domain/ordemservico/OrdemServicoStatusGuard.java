package com.autoservice.domain.ordemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.validation.Error;

import java.util.List;

public final class OrdemServicoStatusGuard {

    private OrdemServicoStatusGuard() {
    }

    public static void exigir(
            final OrdemServicoStatus statusAtual,
            final OrdemServicoStatus statusEsperado,
            final String mensagem
    ) {
        if (statusAtual != statusEsperado) {
            throw DomainException.with(List.of(new Error(mensagem)));
        }
    }

    public static void exigirUmDe(
            final OrdemServicoStatus statusAtual,
            final String mensagem,
            final OrdemServicoStatus... statusPermitidos
    ) {
        for (final OrdemServicoStatus statusPermitido : statusPermitidos) {
            if (statusAtual == statusPermitido) {
                return;
            }
        }

        throw DomainException.with(List.of(new Error(mensagem)));
    }

    public static void proibir(
            final OrdemServicoStatus statusAtual,
            final String mensagem,
            final OrdemServicoStatus... statusProibidos
    ) {
        for (final OrdemServicoStatus statusProibido : statusProibidos) {
            if (statusAtual == statusProibido) {
                throw DomainException.with(List.of(new Error(mensagem)));
            }
        }
    }
}
