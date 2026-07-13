package com.autoservice.application.cliente.query;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteDetailOutput(
        String id,
        String tipoPessoa,
        LocalDate dataCadastro,
        String nome,
        String cpf,
        String razaoSocial,
        String cnpj,
        String email,
        String telefone,
        ClienteOutput.RepresentanteLegalOutput representanteLegal,
        List<VeiculoOutput> veiculos
) {

    public static ClienteDetailOutput from(
            final ClienteOutput cliente,
            final List<VeiculoOutput> veiculos
    ) {
        return new ClienteDetailOutput(
                cliente.id(),
                cliente.tipoPessoa(),
                cliente.dataCadastro(),
                cliente.nome(),
                cliente.cpf(),
                cliente.razaoSocial(),
                cliente.cnpj(),
                cliente.email(),
                cliente.telefone(),
                cliente.representanteLegal(),
                veiculos
        );
    }

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
}
