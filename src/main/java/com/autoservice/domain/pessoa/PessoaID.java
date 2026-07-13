package com.autoservice.domain.pessoa;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class PessoaID extends Identifier {

    private final String value;

    protected PessoaID() {
        this.value = null;
    }

    private PessoaID(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static PessoaID unique() {
        return new PessoaID(UUID.randomUUID().toString().toLowerCase());
    }

    public static PessoaID from(final String id) {
        return new PessoaID(id);
    }

    public static PessoaID from(final UUID id) {
        return new PessoaID(id.toString().toLowerCase());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final PessoaID that = (PessoaID) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
