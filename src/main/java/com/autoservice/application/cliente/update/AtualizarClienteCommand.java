package com.autoservice.application.cliente.update;

import java.util.UUID;

public record AtualizarClienteCommand(
        UUID clienteId,
        String nome,
        String razaoSocial,
        String email,
        String telefone,
        String representanteNome,
        String representanteCpf,
        String representanteEmail,
        String representanteTelefone
) {

    public static AtualizarClienteCommand with(
            final UUID clienteId,
            final String nome,
            final String razaoSocial,
            final String email,
            final String telefone,
            final String representanteNome,
            final String representanteCpf,
            final String representanteEmail,
            final String representanteTelefone
    ) {
        return new AtualizarClienteCommand(
                clienteId,
                nome,
                razaoSocial,
                email,
                telefone,
                representanteNome,
                representanteCpf,
                representanteEmail,
                representanteTelefone
        );
    }
}
