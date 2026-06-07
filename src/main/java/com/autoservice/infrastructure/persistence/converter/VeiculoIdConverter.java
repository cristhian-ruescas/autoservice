package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.veiculo.VeiculoID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class VeiculoIdConverter extends StringIdentifierConverter<VeiculoID> {

    public VeiculoIdConverter() {
        super(VeiculoID::from, VeiculoID::getValue);
    }
}
