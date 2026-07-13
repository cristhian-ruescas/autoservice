package com.autoservice.domain.veiculo;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class VeiculoID extends Identifier {

    private final String valor;

    protected VeiculoID() {
        this.valor = null;
    }

    private VeiculoID(final String valor) {
        Objects.requireNonNull(valor);
        this.valor = valor;
    }

    public static VeiculoID unique() {
        return new VeiculoID(UUID.randomUUID().toString().toLowerCase());
    }

    public static VeiculoID from(final String valor) {
        return new VeiculoID(valor);
    }

    public static VeiculoID from(final UUID valor) {
        return new VeiculoID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VeiculoID veiculoID = (VeiculoID) o;
        return Objects.equals(valor, veiculoID.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
