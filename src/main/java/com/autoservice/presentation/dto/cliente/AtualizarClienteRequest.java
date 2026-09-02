package com.autoservice.presentation.dto.cliente;

public record AtualizarClienteRequest(
        String nome,
        String razaoSocial,
        String email,
        String telefone,
        String representanteNome,
        String representanteCpf,
        String representanteEmail,
        String representanteTelefone
) {
}
