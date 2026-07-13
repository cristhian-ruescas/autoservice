package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemcompra.OrdemCompraID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrdemCompraIdConverter extends StringIdentifierConverter<OrdemCompraID> {

    public OrdemCompraIdConverter() {
        super(OrdemCompraID::from, OrdemCompraID::getValue);
    }
}
