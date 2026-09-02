package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.valueobject.Marca;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MarcaConverter extends ValueObjectStringConverter<Marca> {

    public MarcaConverter() {
        super(Marca::from, Marca::getValue);
    }
}
