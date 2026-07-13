package com.autoservice.application.veiculo.query;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VeiculoOutput(
        String id,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Integer kilometragem,
        ProprietarioOutput proprietario
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProprietarioOutput(
            String tipoPessoa,
            String nome,
            String cpf,
            String razaoSocial,
            String cnpj,
            String email,
            String telefone
    ) {
    }
}
