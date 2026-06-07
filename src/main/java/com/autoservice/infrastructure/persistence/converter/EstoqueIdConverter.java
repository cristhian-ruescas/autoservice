package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.estoque.EstoqueID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EstoqueIdConverter extends StringIdentifierConverter<EstoqueID> {

    public EstoqueIdConverter() {
        super(EstoqueID::from, EstoqueID::getValue);
    }
}
