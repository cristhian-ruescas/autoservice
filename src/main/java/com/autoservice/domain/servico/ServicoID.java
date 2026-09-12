package com.autoservice.domain.servico;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class ServicoID extends Identifier {

    private final String valor;

    protected ServicoID() {
        this.valor = null;
    }

    private ServicoID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ServicoID unique() {
        return new ServicoID(UUID.randomUUID().toString().toLowerCase());
    }

    public static ServicoID from(final String valor) {
        return new ServicoID(valor);
    }

    public static ServicoID from(final UUID valor) {
        return new ServicoID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServicoID servicoID = (ServicoID) o;
        return Objects.equals(valor, servicoID.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
