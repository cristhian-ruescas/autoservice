package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Cor;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CorConverter extends ValueObjectStringConverter<Cor> {

    public CorConverter() {
        super(Cor::from, Cor::getValue);
    }
}
