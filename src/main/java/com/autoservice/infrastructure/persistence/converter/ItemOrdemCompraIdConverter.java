package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ItemOrdemCompraIdConverter implements AttributeConverter<ItemOrdemCompraID, String> {

    @Override
    public String convertToDatabaseColumn(final ItemOrdemCompraID attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ItemOrdemCompraID convertToEntityAttribute(final String dbData) {
        return dbData == null ? null : ItemOrdemCompraID.from(dbData);
    }
}
