package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoCommand;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.delete.RemoverTipoVeiculoCommand;
import com.autoservice.application.tipoveiculo.delete.RemoverTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.query.GetTipoVeiculoByIdQuery;
import com.autoservice.application.tipoveiculo.query.ListTipoVeiculoQuery;
import com.autoservice.application.tipoveiculo.query.TipoVeiculoOutput;
import com.autoservice.application.tipoveiculo.update.AtualizarTipoVeiculoCommand;
import com.autoservice.application.tipoveiculo.update.AtualizarTipoVeiculoUseCase;
import com.autoservice.presentation.dto.tipoveiculo.AtualizarTipoVeiculoRequest;
import com.autoservice.presentation.dto.tipoveiculo.CadastrarTipoVeiculoRequest;
import com.autoservice.presentation.dto.tipoveiculo.TipoVeiculoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tipos-veiculo")
@Tag(name = "Tipos de veículo", description = "Catálogo marca/modelo/ano para veículos e peças.")
public class TipoVeiculoController {

    private final CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase;
    private final ListTipoVeiculoQuery listTipoVeiculoQuery;
    private final GetTipoVeiculoByIdQuery getTipoVeiculoByIdQuery;
    private final AtualizarTipoVeiculoUseCase atualizarTipoVeiculoUseCase;
    private final RemoverTipoVeiculoUseCase removerTipoVeiculoUseCase;

    public TipoVeiculoController(
            final CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase,
            final ListTipoVeiculoQuery listTipoVeiculoQuery,
            final GetTipoVeiculoByIdQuery getTipoVeiculoByIdQuery,
            final AtualizarTipoVeiculoUseCase atualizarTipoVeiculoUseCase,
            final RemoverTipoVeiculoUseCase removerTipoVeiculoUseCase
    ) {
        this.cadastrarTipoVeiculoUseCase = cadastrarTipoVeiculoUseCase;
        this.listTipoVeiculoQuery = listTipoVeiculoQuery;
        this.getTipoVeiculoByIdQuery = getTipoVeiculoByIdQuery;
        this.atualizarTipoVeiculoUseCase = atualizarTipoVeiculoUseCase;
        this.removerTipoVeiculoUseCase = removerTipoVeiculoUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<TipoVeiculoOutput>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String marca,
            @RequestParam(required = false) final String modelo,
            @RequestParam(required = false) final Integer ano
    ) {
        return ResponseEntity.ok(this.listTipoVeiculoQuery.listar(
                page,
                size,
                marca,
                modelo,
                ano
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoVeiculoOutput> buscarPorId(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.getTipoVeiculoByIdQuery.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoVeiculoOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody @Valid final AtualizarTipoVeiculoRequest request
    ) {
        final var output = this.atualizarTipoVeiculoUseCase.execute(AtualizarTipoVeiculoCommand.with(
                id,
                request.marca(),
                request.modelo(),
                request.ano()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerTipoVeiculoUseCase.execute(RemoverTipoVeiculoCommand.with(id));

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Cadastrar ou reutilizar tipo de veículo", description = "Idempotente por marca, modelo e ano (case-insensitive).")
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
