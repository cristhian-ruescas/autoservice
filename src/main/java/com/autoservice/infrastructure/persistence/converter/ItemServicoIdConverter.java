package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.itemservico.ItemServicoID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ItemServicoIdConverter implements AttributeConverter<ItemServicoID, String> {

    @Override
    public String convertToDatabaseColumn(final ItemServicoID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ItemServicoID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : ItemServicoID.from(dbData);
    }
}
