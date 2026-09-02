package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class OrdemCompraID extends Identifier {

    private final String valor;

    protected OrdemCompraID() {
        this.valor = null;
    }

    private OrdemCompraID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static OrdemCompraID unique() {
        return new OrdemCompraID(UUID.randomUUID().toString().toLowerCase());
    }

    public static OrdemCompraID from(final String valor) {
        return new OrdemCompraID(valor);
    }

    public static OrdemCompraID from(final UUID valor) {
        return new OrdemCompraID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final OrdemCompraID that = (OrdemCompraID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
