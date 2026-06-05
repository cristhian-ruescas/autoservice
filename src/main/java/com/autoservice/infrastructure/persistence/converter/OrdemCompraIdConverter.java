package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemcompra.OrdemCompraID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrdemCompraIdConverter implements AttributeConverter<OrdemCompraID, String> {

    @Override
    public String convertToDatabaseColumn(final OrdemCompraID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public OrdemCompraID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : OrdemCompraID.from(dbData);
    }
}
