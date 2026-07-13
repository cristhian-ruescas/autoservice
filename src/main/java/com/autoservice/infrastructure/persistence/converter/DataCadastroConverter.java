package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.cliente.valueobject.DataCadastro;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = false)
public class DataCadastroConverter implements AttributeConverter<DataCadastro, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(final DataCadastro attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public DataCadastro convertToEntityAttribute(final LocalDate dbData) {
        return dbData == null ? null : DataCadastro.from(dbData);
    }
}
