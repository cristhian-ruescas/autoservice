package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.veiculo.delete.RemoverVeiculoCommand;
import com.autoservice.application.veiculo.delete.RemoverVeiculoUseCase;
import com.autoservice.application.veiculo.query.*;
import com.autoservice.application.veiculo.update.AtualizarVeiculoCommand;
import com.autoservice.application.veiculo.update.AtualizarVeiculoUseCase;
import com.autoservice.presentation.dto.veiculo.AtualizarVeiculoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final ListVeiculosQuery listVeiculosQuery;
    private final GetVeiculoByIdQuery getVeiculoByIdQuery;
    private final GetVeiculoByPlacaQuery getVeiculoByPlacaQuery;
    private final ListVeiculosByClienteQuery listVeiculosByClienteQuery;
    private final AtualizarVeiculoUseCase atualizarVeiculoUseCase;
    private final RemoverVeiculoUseCase removerVeiculoUseCase;

    public VeiculoController(
            final ListVeiculosQuery listVeiculosQuery,
            final GetVeiculoByIdQuery getVeiculoByIdQuery,
            final GetVeiculoByPlacaQuery getVeiculoByPlacaQuery,
            final ListVeiculosByClienteQuery listVeiculosByClienteQuery,
            final AtualizarVeiculoUseCase atualizarVeiculoUseCase,
            final RemoverVeiculoUseCase removerVeiculoUseCase
    ) {
        this.listVeiculosQuery = listVeiculosQuery;
        this.getVeiculoByIdQuery = getVeiculoByIdQuery;
        this.getVeiculoByPlacaQuery = getVeiculoByPlacaQuery;
        this.listVeiculosByClienteQuery = listVeiculosByClienteQuery;
        this.atualizarVeiculoUseCase = atualizarVeiculoUseCase;
        this.removerVeiculoUseCase = removerVeiculoUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<VeiculoOutput>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String marca,
            @RequestParam(required = false) final String modelo,
            @RequestParam(required = false) final Integer ano,
            @RequestParam(required = false) final UUID proprietarioId
    ) {
        return ResponseEntity.ok(this.listVeiculosQuery.listar(
                page,
                size,
                marca,
                modelo,
                ano,
                proprietarioId
        ));
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<VeiculoOutput> buscarPorPlaca(@PathVariable final String placa) {
        return ResponseEntity.ok(this.getVeiculoByPlacaQuery.buscarPorPlaca(placa));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VeiculoOutput>> listarPorCliente(@PathVariable final UUID clienteId) {
        return ResponseEntity.ok(this.listVeiculosByClienteQuery.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoOutput> buscarPorId(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.getVeiculoByIdQuery.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody final AtualizarVeiculoRequest request
    ) {
        final var output = this.atualizarVeiculoUseCase.execute(AtualizarVeiculoCommand.with(
                id,
                request.placa(),
                request.marca(),
                request.modelo(),
                request.ano(),
                request.cor(),
                request.kilometragem()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerVeiculoUseCase.execute(RemoverVeiculoCommand.with(id));

        return ResponseEntity.noContent().build();
    }
}
