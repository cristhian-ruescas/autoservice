package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoQuery;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoCommand;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoUseCase;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoOutput;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoUseCase;
import com.autoservice.presentation.dto.ordemservico.AtualizarOrdemServicoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(name = "Ordens de serviço", description = "Consulta e manutenção básica da ordem de serviço.")
public class OrdemServicoConsultaController {

    private final ListOrdemServicoQuery listOrdemServicoQuery;
    private final DetailOrdemServicoQuery detailOrdemServicoQuery;
    private final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery;
    private final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase;
    private final RemoverOrdemServicoUseCase removerOrdemServicoUseCase;

    public OrdemServicoConsultaController(
            final ListOrdemServicoQuery listOrdemServicoQuery,
            final DetailOrdemServicoQuery detailOrdemServicoQuery,
            final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery,
            final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase,
            final RemoverOrdemServicoUseCase removerOrdemServicoUseCase
    ) {
        this.listOrdemServicoQuery = listOrdemServicoQuery;
        this.detailOrdemServicoQuery = detailOrdemServicoQuery;
        this.acompanharOrdemServicoQuery = acompanharOrdemServicoQuery;
        this.atualizarOrdemServicoUseCase = atualizarOrdemServicoUseCase;
        this.removerOrdemServicoUseCase = removerOrdemServicoUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<ListOrdemServicoOutput>> list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String status
    ) {
        return ResponseEntity.ok(this.listOrdemServicoQuery.execute(page, size, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar ordem de serviço", description = "Inclui veículo, cliente, itens e valor total.")
    public ResponseEntity<DetailOrdemServicoOutput> detail(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.detailOrdemServicoQuery.execute(id));
    }

    @GetMapping("/{id}/andamento")
    public ResponseEntity<AcompanharOrdemServicoOutput> acompanhar(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.acompanharOrdemServicoQuery.acompanhar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtualizarOrdemServicoOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody @Valid final AtualizarOrdemServicoRequest request
    ) {
        return ResponseEntity.ok(this.atualizarOrdemServicoUseCase.execute(AtualizarOrdemServicoCommand.with(
                id,
                request.veiculoId(),
                request.relato(),
                request.tempoPrevistoExecucaoDias(),
                request.tempoPrevistoExecucaoHoras()
        )));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerOrdemServicoUseCase.execute(RemoverOrdemServicoCommand.with(id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
