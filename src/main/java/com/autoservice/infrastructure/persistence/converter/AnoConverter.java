package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Ano;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class AnoConverter implements AttributeConverter<Ano, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final Ano attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Ano convertToEntityAttribute(final Integer dbData) {
        return dbData == null ? null : Ano.from(dbData);
    }
}
