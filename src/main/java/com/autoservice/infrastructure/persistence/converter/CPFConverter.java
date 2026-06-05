package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.CPF;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CPFConverter implements AttributeConverter<CPF, String> {

    @Override
    public String convertToDatabaseColumn(final CPF attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public CPF convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : CPF.from(dbData);
    }
}
