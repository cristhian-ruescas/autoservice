package com.autoservice.infrastructure.persistence.converter;

import com.autoservice.domain.pessoa.PessoaID;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PessoaIdConverter extends StringIdentifierConverter<PessoaID> {

    public PessoaIdConverter() {
        super(PessoaID::from, PessoaID::getValue);
    }
}
