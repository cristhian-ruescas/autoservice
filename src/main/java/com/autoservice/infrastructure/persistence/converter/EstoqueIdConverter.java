package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.estoque.EstoqueID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EstoqueIdConverter implements AttributeConverter<EstoqueID, String> {

    @Override
    public String convertToDatabaseColumn(final EstoqueID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public EstoqueID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : EstoqueID.from(dbData);
    }
}
