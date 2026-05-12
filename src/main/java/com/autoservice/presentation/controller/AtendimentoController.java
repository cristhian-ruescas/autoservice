package com.autoservice.presentation.controller;

import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atendimentos")
@Tag(
        name = "Atendimentos",
        description = "Abertura de atendimento: cadastro de cliente/pessoa, veículo e ordem de serviço inicial (status RECEBIDO)."
)
public class AtendimentoController {

    private final AbrirAtendimentoUseCase abrirAtendimentoUseCase;

    public AtendimentoController(final AbrirAtendimentoUseCase abrirAtendimentoUseCase) {
        this.abrirAtendimentoUseCase = abrirAtendimentoUseCase;
    }

    @PostMapping
    @Operation(summary = "Abrir atendimento / criar OS", description = "Primeiro passo do fluxo: OS criada em RECEBIDO com cliente e veículo.")
    public ResponseEntity<AbrirAtendimentoResponse> abrir(
            @RequestBody @Valid final AbrirAtendimentoRequest request
    ) {
        final var command = AbrirAtendimentoCommand.with(
                TipoPessoaAtendimento.valueOf(request.tipoPessoa().trim().toUpperCase()),
                request.nome(),
                request.cpf(),
                request.razaoSocial(),
                request.cnpj(),
                request.representanteNome(),
                request.representanteCpf(),
                request.representanteEmail(),
                request.representanteTelefone(),
                request.email(),
                request.telefone(),
                request.placa(),
                request.marca(),
                request.modelo(),
                request.ano(),
                request.cor(),
                request.kilometragem(),
                request.relato()
        );

        final var output = this.abrirAtendimentoUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AbrirAtendimentoResponse.from(output));
    }
}
