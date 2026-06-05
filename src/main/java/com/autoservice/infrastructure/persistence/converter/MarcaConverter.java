package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Marca;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MarcaConverter implements AttributeConverter<Marca, String> {

    @Override
    public String convertToDatabaseColumn(final Marca attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Marca convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Marca.from(dbData);
    }
}
