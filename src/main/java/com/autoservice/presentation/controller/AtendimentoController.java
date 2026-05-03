package com.autoservice.presentation.controller;

import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AbrirAtendimentoUseCase abrirAtendimentoUseCase;

    public AtendimentoController(final AbrirAtendimentoUseCase abrirAtendimentoUseCase) {
        this.abrirAtendimentoUseCase = abrirAtendimentoUseCase;
    }

    @PostMapping
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
