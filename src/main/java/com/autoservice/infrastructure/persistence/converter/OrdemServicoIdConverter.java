package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrdemServicoIdConverter extends StringIdentifierConverter<OrdemServicoID> {

    public OrdemServicoIdConverter() {
        super(OrdemServicoID::from, OrdemServicoID::getValue);
    }
}
