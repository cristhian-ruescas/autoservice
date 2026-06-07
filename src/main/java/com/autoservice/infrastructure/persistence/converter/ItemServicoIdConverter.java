package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.itemservico.ItemServicoID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ItemServicoIdConverter extends StringIdentifierConverter<ItemServicoID> {

    public ItemServicoIdConverter() {
        super(ItemServicoID::from, ItemServicoID::getValue);
    }
}
