package com.autoservice.application.cliente.create;

import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;

import java.util.List;

public record CadastrarClienteCommand(
        TipoPessoaAtendimento tipoPessoa,
        String nome,
        String cpf,
        String razaoSocial,
        String cnpj,
        String representanteNome,
        String representanteCpf,
        String representanteEmail,
        String representanteTelefone,
        String email,
        String telefone
) {

    public static CadastrarClienteCommand with(
            final TipoPessoaAtendimento tipoPessoa,
            final String nome,
            final String cpf,
            final String razaoSocial,
            final String cnpj,
            final String representanteNome,
            final String representanteCpf,
            final String representanteEmail,
            final String representanteTelefone,
            final String email,
            final String telefone
    ) {
        return new CadastrarClienteCommand(
                tipoPessoa,
                nome,
                cpf,
                razaoSocial,
                cnpj,
                representanteNome,
                representanteCpf,
                representanteEmail,
                representanteTelefone,
                email,
                telefone
        );
    }
}
