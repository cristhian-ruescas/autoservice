package com.autoservice.application.atendimento.create;

import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;

import java.util.List;

public record AbrirAtendimentoCommand(
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
        String telefone,
        String placa,
        String marca,
        String modelo,
        Integer ano,
        String cor,
        Integer kilometragem,
        String relato,
        List<AbrirAtendimentoItemCommand> itens
) {
    public static AbrirAtendimentoCommand with(
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
            final String telefone,
            final String placa,
            final String marca,
            final String modelo,
            final Integer ano,
            final String cor,
            final Integer kilometragem,
            final String relato,
            final List<AbrirAtendimentoItemCommand> itens
    ) {
        return new AbrirAtendimentoCommand(
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
                telefone,
                placa,
                marca,
                modelo,
                ano,
                cor,
                kilometragem,
                relato,
                itens == null ? List.of() : List.copyOf(itens)
        );
    }
}
