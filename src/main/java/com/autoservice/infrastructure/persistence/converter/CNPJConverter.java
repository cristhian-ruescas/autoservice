package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.CNPJ;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CNPJConverter extends ValueObjectStringConverter<CNPJ> {

    public CNPJConverter() {
        super(CNPJ::from, CNPJ::getValue);
    }
}
