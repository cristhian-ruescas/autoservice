package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.peca.PecaID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PecaIdConverter extends StringIdentifierConverter<PecaID> {

    public PecaIdConverter() {
        super(PecaID::from, PecaID::getValue);
    }
}
