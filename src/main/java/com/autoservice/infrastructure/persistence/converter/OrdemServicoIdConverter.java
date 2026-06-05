package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrdemServicoIdConverter implements AttributeConverter<OrdemServicoID, String> {

    @Override
    public String convertToDatabaseColumn(final OrdemServicoID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public OrdemServicoID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : OrdemServicoID.from(dbData);
    }
}
