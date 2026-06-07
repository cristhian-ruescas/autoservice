package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.cliente.ClienteID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ClienteIdConverter extends StringIdentifierConverter<ClienteID> {

    public ClienteIdConverter() {
        super(ClienteID::from, ClienteID::getValue);
    }
}
