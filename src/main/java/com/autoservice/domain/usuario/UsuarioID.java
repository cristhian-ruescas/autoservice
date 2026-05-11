package com.autoservice.domain.usuario;

import com.autoservice.domain.Identifier;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class UsuarioID extends Identifier {
    @Column(name = "id", nullable = false, updatable = false)
    private String value;

    public UsuarioID() {
        this.value = UUID.randomUUID().toString();
    }

    private UsuarioID(String value) {
        this.value = value;
    }

    public static UsuarioID unique() {
        return new UsuarioID(UUID.randomUUID().toString());
    }

    public static UsuarioID from(String value) {
        return new UsuarioID(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioID usuarioID = (UsuarioID) o;
        return Objects.equals(value, usuarioID.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
