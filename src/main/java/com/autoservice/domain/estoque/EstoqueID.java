package com.autoservice.domain.estoque;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class EstoqueID extends Identifier {

    private final String valor;

    protected EstoqueID() {
        this.valor = null;
    }

    private EstoqueID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static EstoqueID unique() {
        return new EstoqueID(UUID.randomUUID().toString().toLowerCase());
    }

    public static EstoqueID from(final String valor) {
        return new EstoqueID(valor);
    }

    public static EstoqueID from(final UUID valor) {
        return new EstoqueID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final EstoqueID estoqueID = (EstoqueID) o;
        return Objects.equals(valor, estoqueID.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
