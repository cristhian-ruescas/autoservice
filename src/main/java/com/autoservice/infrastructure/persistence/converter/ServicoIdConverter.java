package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.servico.ServicoID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ServicoIdConverter extends StringIdentifierConverter<ServicoID> {

    public ServicoIdConverter() {
        super(ServicoID::from, ServicoID::getValue);
    }
}
