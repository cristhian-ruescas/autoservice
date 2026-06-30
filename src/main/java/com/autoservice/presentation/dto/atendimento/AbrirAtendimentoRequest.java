package com.autoservice.presentation.dto.atendimento;

import com.autoservice.presentation.dto.ordemservico.AdicionarItemServicoRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AbrirAtendimentoRequest(
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
        @NotBlank String telefone,
        @NotBlank String placa,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotNull @Min(1900) Integer ano,
        @NotBlank String cor,
        @NotNull @Min(0) Integer kilometragem,
        @NotBlank String relato,
        List<AdicionarItemServicoRequest> itens
) {
}
