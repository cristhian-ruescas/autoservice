package com.autoservice.domain.peca;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PecaID extends Identifier {

    @Column(name = "id", nullable = false, updatable = false)
    private final String valor;

    protected PecaID() {
        this.valor = null;
    }

    private PecaID(final String valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static PecaID unique() {
        return new PecaID(UUID.randomUUID().toString().toLowerCase());
    }

    public static PecaID from(final String valor) {
        return new PecaID(valor);
    }

    public static PecaID from(final UUID valor) {
        return new PecaID(valor.toString().toLowerCase());
    }

    public String getValue() {
        return valor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final PecaID pecaID = (PecaID) o;
        return Objects.equals(valor, pecaID.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }
}
