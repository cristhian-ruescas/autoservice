package com.autoservice.domain.ordemservico.valueobject;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;

import java.util.List;

public record TempoPrevistoExecucao(int dias, int horas) {

    public static TempoPrevistoExecucao of(final Integer dias, final Integer horas) {
        final int diasNormalizados = dias == null ? 0 : dias;
        final int horasNormalizadas = horas == null ? 0 : horas;

        if (diasNormalizados < 0) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução em dias precisa ser maior ou igual a zero")
            ));
        }
        if (horasNormalizadas < 0 || horasNormalizadas > 23) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução em horas precisa estar entre 0 e 23")
            ));
        }
        if (diasNormalizados == 0 && horasNormalizadas == 0) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução precisa ser maior que zero")
            ));
        }

        return new TempoPrevistoExecucao(diasNormalizados, horasNormalizadas);
    }

    public String formatarParaRelatorio() {
        final var partes = new java.util.ArrayList<String>();

        if (dias > 0) {
            partes.add(dias + " dia(s)");
        }
        if (horas > 0) {
            partes.add(horas + " hora(s)");
        }

        return String.join(" e ", partes);
    }
}
