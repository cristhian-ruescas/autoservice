package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CNPJConverter implements AttributeConverter<CNPJ, String> {

    @Override
    public String convertToDatabaseColumn(final CNPJ attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public CNPJ convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : CNPJ.from(dbData);
    }
}
