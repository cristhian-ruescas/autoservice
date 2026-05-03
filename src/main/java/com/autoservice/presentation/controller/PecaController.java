package com.autoservice.presentation.controller;

import com.autoservice.application.peca.create.CadastrarPecaCommand;
import com.autoservice.application.peca.create.CadastrarPecaUseCase;
import com.autoservice.presentation.dto.peca.CadastrarPecaRequest;
import com.autoservice.presentation.dto.peca.CadastrarPecaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pecas")
public class PecaController {

    private final CadastrarPecaUseCase cadastrarPecaUseCase;

    public PecaController(final CadastrarPecaUseCase cadastrarPecaUseCase) {
        this.cadastrarPecaUseCase = cadastrarPecaUseCase;
    }

    @PostMapping
    public ResponseEntity<CadastrarPecaResponse> create(
            @RequestBody @Valid final CadastrarPecaRequest request
    ) {
        final var output = this.cadastrarPecaUseCase.execute(CadastrarPecaCommand.with(
                request.descricao(),
                request.codigo(),
                request.marca(),
                request.valorUnitario(),
                request.tipoVeiculoId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CadastrarPecaResponse.from(output));
    }
}
