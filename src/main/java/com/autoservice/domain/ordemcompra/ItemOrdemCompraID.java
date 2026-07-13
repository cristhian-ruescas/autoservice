package com.autoservice.domain.ordemcompra;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class ItemOrdemCompraID extends Identifier {

    private final String valor;

    protected ItemOrdemCompraID() {
        this.valor = null;
    }

    private ItemOrdemCompraID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ItemOrdemCompraID unique() {
        return new ItemOrdemCompraID(UUID.randomUUID().toString().toLowerCase());
    }

    public static ItemOrdemCompraID from(final String valor) {
        return new ItemOrdemCompraID(valor);
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ItemOrdemCompraID that = (ItemOrdemCompraID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
