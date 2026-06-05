package com.autoservice.domain.tipoveiculo;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class TipoVeiculoID extends Identifier {

    private final String valor;

    protected TipoVeiculoID() {
        this.valor = null;
    }

    private TipoVeiculoID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static TipoVeiculoID unique() {
        return new TipoVeiculoID(UUID.randomUUID().toString().toLowerCase());
    }

    public static TipoVeiculoID from(final String valor) {
        return new TipoVeiculoID(valor);
    }

    public static TipoVeiculoID from(final UUID valor) {
        return new TipoVeiculoID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final TipoVeiculoID that = (TipoVeiculoID) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
