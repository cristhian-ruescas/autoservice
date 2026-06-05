package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.PessoaID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PessoaIdConverter implements AttributeConverter<PessoaID, String> {

    @Override
    public String convertToDatabaseColumn(final PessoaID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public PessoaID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : PessoaID.from(dbData);
    }
}
