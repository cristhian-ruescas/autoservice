package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.peca.create.CadastrarPecaCommand;
import com.autoservice.application.peca.create.CadastrarPecaUseCase;
import com.autoservice.application.peca.delete.RemoverPecaCommand;
import com.autoservice.application.peca.delete.RemoverPecaUseCase;
import com.autoservice.application.peca.query.GetPecaByIdQuery;
import com.autoservice.application.peca.query.ListPecasQuery;
import com.autoservice.application.peca.query.PecaOutput;
import com.autoservice.application.peca.update.AtualizarPecaCommand;
import com.autoservice.application.peca.update.AtualizarPecaUseCase;
import com.autoservice.presentation.dto.peca.AtualizarPecaRequest;
import com.autoservice.presentation.dto.peca.CadastrarPecaRequest;
import com.autoservice.presentation.dto.peca.CadastrarPecaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/pecas")
public class PecaController {

    private final CadastrarPecaUseCase cadastrarPecaUseCase;
    private final ListPecasQuery listPecasQuery;
    private final GetPecaByIdQuery getPecaByIdQuery;
    private final AtualizarPecaUseCase atualizarPecaUseCase;
    private final RemoverPecaUseCase removerPecaUseCase;

    public PecaController(
            final CadastrarPecaUseCase cadastrarPecaUseCase,
            final ListPecasQuery listPecasQuery,
            final GetPecaByIdQuery getPecaByIdQuery,
            final AtualizarPecaUseCase atualizarPecaUseCase,
            final RemoverPecaUseCase removerPecaUseCase
    ) {
        this.cadastrarPecaUseCase = cadastrarPecaUseCase;
        this.listPecasQuery = listPecasQuery;
        this.getPecaByIdQuery = getPecaByIdQuery;
        this.atualizarPecaUseCase = atualizarPecaUseCase;
        this.removerPecaUseCase = removerPecaUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<PecaOutput>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String marca,
            @RequestParam(required = false) final String codigo
    ) {
        return ResponseEntity.ok(this.listPecasQuery.listar(page, size, marca, codigo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PecaOutput> buscarPorId(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.getPecaByIdQuery.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PecaOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody final AtualizarPecaRequest request
    ) {
        final var output = this.atualizarPecaUseCase.execute(AtualizarPecaCommand.with(
                id,
                request.descricao(),
                request.codigo(),
                request.marca(),
                request.valorUnitario(),
                request.tipoVeiculoId()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerPecaUseCase.execute(RemoverPecaCommand.with(id));

        return ResponseEntity.noContent().build();
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
