package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TipoVeiculoIdConverter extends StringIdentifierConverter<TipoVeiculoID> {

    public TipoVeiculoIdConverter() {
        super(TipoVeiculoID::from, TipoVeiculoID::getValue);
    }
}
