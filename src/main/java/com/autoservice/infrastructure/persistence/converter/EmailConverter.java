package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(final Email attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Email convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : Email.from(dbData);
    }
}
