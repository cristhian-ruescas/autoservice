package com.autoservice.presentation.controller;

import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraCommand;
import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraUseCase;
import com.autoservice.application.ordemcompra.query.OrdemCompraQuery;
import com.autoservice.presentation.dto.ordemcompra.OrdemCompraDetailResponse;
import com.autoservice.presentation.dto.ordemcompra.OrdemCompraResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordens-compra")
@Tag(
        name = "Ordens de compra",
        description = "Pedidos de peças gerados a partir do fluxo da OS (ex.: após aprovação). Listagem, detalhe e realização."
)
public class OrdemCompraController {

    private final RealizarOrdemCompraUseCase realizarOrdemCompraUseCase;
    private final OrdemCompraQuery ordemCompraQuery;

    public OrdemCompraController(
            final RealizarOrdemCompraUseCase realizarOrdemCompraUseCase,
            final OrdemCompraQuery ordemCompraQuery
    ) {
        this.realizarOrdemCompraUseCase = realizarOrdemCompraUseCase;
        this.ordemCompraQuery = ordemCompraQuery;
    }

    @GetMapping
    @Operation(summary = "Listar ordens de compra")
    public ResponseEntity<List<OrdemCompraDetailResponse>> listar() {
        final var response = this.ordemCompraQuery.listar()
                .stream()
                .map(OrdemCompraDetailResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar ordem de compra")
    public ResponseEntity<OrdemCompraDetailResponse> detalhar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemCompraDetailResponse.from(this.ordemCompraQuery.detalhar(id)));
    }

    @PatchMapping("/{id}/realizar")
    @Operation(summary = "Realizar ordem de compra", description = "Confirma compra e aciona atualização de estoque no domínio.")
    public ResponseEntity<OrdemCompraResponse> realizar(@PathVariable final UUID id) {
        final var output = this.realizarOrdemCompraUseCase.execute(RealizarOrdemCompraCommand.with(id));

        return ResponseEntity.ok(OrdemCompraResponse.from(output));
    }
}
