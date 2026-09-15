package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.CPF;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CPFConverter extends ValueObjectStringConverter<CPF> {

    public CPFConverter() {
        super(CPF::from, CPF::getValue);
    }
}
