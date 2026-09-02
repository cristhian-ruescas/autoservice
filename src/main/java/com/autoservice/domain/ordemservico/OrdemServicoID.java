package com.autoservice.domain.ordemservico;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class OrdemServicoID extends Identifier {

    private final String valor;

    protected OrdemServicoID() {
        this.valor = null;
    }

    private OrdemServicoID(final String valor) {
        Objects.requireNonNull(valor);
        this.valor = valor;
    }

    public static OrdemServicoID unique() {
        return new OrdemServicoID(UUID.randomUUID().toString().toLowerCase());
    }

    public static OrdemServicoID from(final String valor) {
        return new OrdemServicoID(valor);
    }

    public static OrdemServicoID from(final UUID valor) {
        return new OrdemServicoID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final OrdemServicoID that = (OrdemServicoID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
