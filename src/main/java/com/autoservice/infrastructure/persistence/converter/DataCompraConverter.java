package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

@Converter(autoApply = false)
public class DataCompraConverter implements AttributeConverter<DataCompra, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(final DataCompra attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public DataCompra convertToEntityAttribute(final LocalDate dbData) {
        return dbData == null ? null : DataCompra.from(dbData);
    }
}
