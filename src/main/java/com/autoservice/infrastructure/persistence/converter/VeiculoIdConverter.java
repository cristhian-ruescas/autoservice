package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.VeiculoID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class VeiculoIdConverter implements AttributeConverter<VeiculoID, String> {

    @Override
    public String convertToDatabaseColumn(final VeiculoID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public VeiculoID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : VeiculoID.from(dbData);
    }
}
