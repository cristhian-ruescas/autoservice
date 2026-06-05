package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Cor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CorConverter implements AttributeConverter<Cor, String> {

    @Override
    public String convertToDatabaseColumn(final Cor attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Cor convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Cor.from(dbData);
    }
}
