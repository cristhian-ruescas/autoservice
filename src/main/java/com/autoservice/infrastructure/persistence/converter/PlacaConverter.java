package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Placa;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PlacaConverter implements AttributeConverter<Placa, String> {

    @Override
    public String convertToDatabaseColumn(final Placa attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Placa convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Placa.from(dbData);
    }
}
