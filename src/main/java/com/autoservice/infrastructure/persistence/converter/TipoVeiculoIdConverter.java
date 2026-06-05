package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoVeiculoIdConverter implements AttributeConverter<TipoVeiculoID, String> {

    @Override
    public String convertToDatabaseColumn(final TipoVeiculoID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public TipoVeiculoID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : TipoVeiculoID.from(dbData);
    }
}
