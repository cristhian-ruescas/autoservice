package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.Telefone;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TelefoneConverter implements AttributeConverter<Telefone, String> {

    @Override
    public String convertToDatabaseColumn(final Telefone attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Telefone convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Telefone.from(dbData);
    }
}
