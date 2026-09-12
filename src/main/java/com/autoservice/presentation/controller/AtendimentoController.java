package com.autoservice.presentation.controller;

import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoResponse;
import com.autoservice.presentation.mapper.AtendimentoRequestMapper;
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
        description = "Abertura de atendimento: cadastro de cliente/pessoa, veículo, ordem de serviço e itens de orçamento (serviços/peças) em uma única chamada."
)
public class AtendimentoController {

    private final AbrirAtendimentoUseCase abrirAtendimentoUseCase;

    public AtendimentoController(final AbrirAtendimentoUseCase abrirAtendimentoUseCase) {
        this.abrirAtendimentoUseCase = abrirAtendimentoUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Abrir atendimento / criar OS",
            description = "Cria cliente, veículo e OS. Opcionalmente inclui serviços e peças; nesse caso a OS inicia em EM_DIAGNOSTICO."
    )
    public ResponseEntity<AbrirAtendimentoResponse> abrir(
            @RequestBody @Valid final AbrirAtendimentoRequest request
    ) {
        final var command = AtendimentoRequestMapper.toCommand(request);

        final var output = this.abrirAtendimentoUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AbrirAtendimentoResponse.from(output));
    }
}
