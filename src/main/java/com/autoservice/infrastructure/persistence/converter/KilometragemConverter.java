package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class KilometragemConverter implements AttributeConverter<Kilometragem, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final Kilometragem attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Kilometragem convertToEntityAttribute(final Integer dbData) {
        return dbData == null ? null : Kilometragem.from(dbData);
    }
}
