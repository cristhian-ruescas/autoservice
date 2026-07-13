package com.autoservice.domain.usuario;

import com.autoservice.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

public class UsuarioID extends Identifier {
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

    public String getValue() {
        return value;
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
