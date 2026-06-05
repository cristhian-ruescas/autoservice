package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.peca.PecaID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PecaIdConverter implements AttributeConverter<PecaID, String> {

    @Override
    public String convertToDatabaseColumn(final PecaID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PecaID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : PecaID.from(dbData);
    }
}
