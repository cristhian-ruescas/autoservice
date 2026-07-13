package com.autoservice.domain.cliente;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class ClienteID extends Identifier {

    private final String valor;

    protected ClienteID() {
        this.valor = null;
    }

    private ClienteID(final String valor) {
        Objects.requireNonNull(valor);
        this.valor = valor;
    }

    public static ClienteID unique() {
        return new ClienteID(UUID.randomUUID().toString().toLowerCase());
    }

    public static ClienteID from(final String valor) {
        return new ClienteID(valor);
    }

    public static ClienteID from(final UUID valor) {
        return new ClienteID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final ClienteID that = (ClienteID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
