package com.autoservice.domain.itemservico;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class ItemServicoID extends Identifier {

    private final String valor;

    protected ItemServicoID() {
        this.valor = null;
    }

    private ItemServicoID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ItemServicoID unique() {
        return new ItemServicoID(UUID.randomUUID().toString().toLowerCase());
    }

    public static ItemServicoID from(final String valor) {
        return new ItemServicoID(valor);
    }

    public static ItemServicoID from(final UUID valor) {
        return new ItemServicoID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ItemServicoID that = (ItemServicoID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
