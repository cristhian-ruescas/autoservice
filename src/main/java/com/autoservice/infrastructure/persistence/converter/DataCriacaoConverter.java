package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = false)
public class DataCriacaoConverter implements AttributeConverter<DataCriacao, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(final DataCriacao attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public DataCriacao convertToEntityAttribute(final LocalDate dbData) {
        return dbData == null ? null : DataCriacao.from(dbData);
    }
}
