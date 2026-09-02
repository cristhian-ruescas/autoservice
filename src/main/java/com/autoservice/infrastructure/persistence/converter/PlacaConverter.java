package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Placa;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PlacaConverter extends ValueObjectStringConverter<Placa> {

    public PlacaConverter() {
        super(Placa::from, Placa::getValue);
    }
}
