package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.Telefone;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TelefoneConverter extends ValueObjectStringConverter<Telefone> {

    public TelefoneConverter() {
        super(Telefone::from, Telefone::getValue);
    }
}
