package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueCommand;
import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueUseCase;
import com.autoservice.application.estoque.query.EstoqueQuery;
import com.autoservice.presentation.dto.estoque.AtualizarLocalizacaoEstoqueRequest;
import com.autoservice.presentation.dto.estoque.AtualizarLocalizacaoEstoqueResponse;
import com.autoservice.presentation.dto.estoque.EstoqueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/estoques")
@Tag(name = "Estoques", description = "Consulta de estoque e atualização de localização física.")
public class EstoqueController {

    private final EstoqueQuery estoqueQuery;
    private final AtualizarLocalizacaoEstoqueUseCase atualizarLocalizacaoEstoqueUseCase;

    public EstoqueController(
            final EstoqueQuery estoqueQuery,
            final AtualizarLocalizacaoEstoqueUseCase atualizarLocalizacaoEstoqueUseCase
    ) {
        this.estoqueQuery = estoqueQuery;
        this.atualizarLocalizacaoEstoqueUseCase = atualizarLocalizacaoEstoqueUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<EstoqueResponse>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size
    ) {
        final var output = this.estoqueQuery.listar(page, size);
        final var response = output.items()
                .stream()
                .map(EstoqueResponse::from)
                .toList();

        return ResponseEntity.ok(PaginationOutput.from(
                response,
                output.page(),
                output.size(),
                output.totalElements()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar estoque")
    public ResponseEntity<EstoqueResponse> detalhar(@PathVariable final UUID id) {
        return ResponseEntity.ok(EstoqueResponse.from(this.estoqueQuery.detalhar(id)));
    }

    @PatchMapping("/{id}/localizacao")
    @Operation(summary = "Atualizar localização do estoque")
    public ResponseEntity<AtualizarLocalizacaoEstoqueResponse> atualizarLocalizacao(
            @PathVariable final UUID id,
            @RequestBody final AtualizarLocalizacaoEstoqueRequest request
    ) {
        final var output = this.atualizarLocalizacaoEstoqueUseCase.execute(
                AtualizarLocalizacaoEstoqueCommand.with(id, request.localizacao())
        );

        return ResponseEntity.ok(AtualizarLocalizacaoEstoqueResponse.from(output));
    }
}
