package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.usuario.UsuarioID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UsuarioIdConverter implements AttributeConverter<UsuarioID, String> {

    @Override
    public String convertToDatabaseColumn(final UsuarioID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UsuarioID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : UsuarioID.from(dbData);
    }
}
