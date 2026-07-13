package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Modelo;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ModeloConverter extends ValueObjectStringConverter<Modelo> {

    public ModeloConverter() {
        super(Modelo::from, Modelo::getValue);
    }
}
