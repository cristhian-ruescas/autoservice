package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Modelo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ModeloConverter implements AttributeConverter<Modelo, String> {

    @Override
    public String convertToDatabaseColumn(final Modelo attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Modelo convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Modelo.from(dbData);
    }
}
