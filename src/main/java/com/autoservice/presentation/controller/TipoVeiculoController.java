package com.autoservice.presentation.controller;

import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoCommand;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoUseCase;
import com.autoservice.presentation.dto.tipoveiculo.CadastrarTipoVeiculoRequest;
import com.autoservice.presentation.dto.tipoveiculo.TipoVeiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tipos-veiculo")
public class TipoVeiculoController {

    private final CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase;

    public TipoVeiculoController(final CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase) {
        this.cadastrarTipoVeiculoUseCase = cadastrarTipoVeiculoUseCase;
    }

    @PostMapping
    public ResponseEntity<TipoVeiculoResponse> create(
            @RequestBody @Valid final CadastrarTipoVeiculoRequest request
    ) {
        final var output = this.cadastrarTipoVeiculoUseCase.execute(CadastrarTipoVeiculoCommand.with(
                request.marca(),
                request.modelo(),
                request.ano()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(TipoVeiculoResponse.from(output));
    }
}
