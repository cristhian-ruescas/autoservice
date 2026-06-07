package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ItemOrdemCompraIdConverter extends StringIdentifierConverter<ItemOrdemCompraID> {

    public ItemOrdemCompraIdConverter() {
        super(ItemOrdemCompraID::from, ItemOrdemCompraID::getValue);
    }
}
