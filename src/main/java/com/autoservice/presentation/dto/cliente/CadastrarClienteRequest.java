package com.autoservice.presentation.dto.cliente;

import jakarta.validation.constraints.NotBlank;

public record CadastrarClienteRequest(
        @NotBlank String tipoPessoa,
        String nome,
        String cpf,
        String razaoSocial,
        String cnpj,
        String representanteNome,
        String representanteCpf,
        String representanteEmail,
        String representanteTelefone,
        @NotBlank String email,
        @NotBlank String telefone
) {
}
