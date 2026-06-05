package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.cliente.ClienteID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ClienteIdConverter implements AttributeConverter<ClienteID, String> {

    @Override
    public String convertToDatabaseColumn(final ClienteID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ClienteID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : ClienteID.from(dbData);
    }
}
