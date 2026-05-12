package com.autoservice.application.cliente.query;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteOutput(
        String id,
        String tipoPessoa,
        LocalDate dataCadastro,
        String nome,
        String cpf,
        String razaoSocial,
        String cnpj,
        String email,
        String telefone,
        RepresentanteLegalOutput representanteLegal
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RepresentanteLegalOutput(
            String id,
            String nome,
            String cpf,
            String email,
            String telefone
    ) {
    }
}
