package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.servico.ServicoID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ServicoIdConverter implements AttributeConverter<ServicoID, String> {

    @Override
    public String convertToDatabaseColumn(final ServicoID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ServicoID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : ServicoID.from(dbData);
    }
}
