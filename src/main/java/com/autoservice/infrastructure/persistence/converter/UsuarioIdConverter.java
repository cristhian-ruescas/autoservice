package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.usuario.UsuarioID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UsuarioIdConverter extends StringIdentifierConverter<UsuarioID> {

    public UsuarioIdConverter() {
        super(UsuarioID::from, UsuarioID::getValue);
    }
}
