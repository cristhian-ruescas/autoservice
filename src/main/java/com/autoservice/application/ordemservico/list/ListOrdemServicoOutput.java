package com.autoservice.application.ordemservico.list;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListOrdemServicoOutput(
        String ordemServicoId,
        String status,
        LocalDate dataCriacao,
        String relato,
        Integer tempoPrevistoExecucaoDias,
        Integer tempoPrevistoExecucaoHoras,
        LocalDateTime iniciadoEm,
        LocalDateTime finalizadoEm,
        VeiculoOutput veiculo,
        ClienteOutput cliente
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record VeiculoOutput(
            String id,
            String placa,
            String marca,
            String modelo,
            Integer ano,
            String cor,
            Integer kilometragem
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ClienteOutput(
            String id,
            String tipoPessoa,
            String nome,
            String cpf,
            String razaoSocial,
            String cnpj,
            String email,
            String telefone,
            RepresentanteLegalOutput representanteLegal
    ) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RepresentanteLegalOutput(
            String nome,
            String cpf,
            String email,
            String telefone
    ) {
    }
}
