package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.valueobject.Email;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class EmailConverter extends ValueObjectStringConverter<Email> {

    public EmailConverter() {
        super(Email::from, Email::getValue);
    }
}
