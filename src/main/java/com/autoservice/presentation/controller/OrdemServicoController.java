package com.autoservice.presentation.controller;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoCommand;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.itemservico.AdicionarItemServicoCommand;
import com.autoservice.application.ordemservico.itemservico.AdicionarItensServicoCommand;
import com.autoservice.application.ordemservico.itemservico.AdicionarItensServicoUseCase;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.presentation.dto.ordemservico.AdicionarItemServicoRequest;
import com.autoservice.presentation.dto.ordemservico.AdicionarItensServicoRequest;
import com.autoservice.presentation.dto.ordemservico.AdicionarItensServicoResponse;
import com.autoservice.presentation.dto.ordemservico.FinalizarDiagnosticoRequest;
import com.autoservice.presentation.dto.ordemservico.OrdemServicoStatusResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;
    private final ListOrdemServicoQuery listOrdemServicoQuery;
    private final DetailOrdemServicoQuery detailOrdemServicoQuery;
    private final AdicionarItensServicoUseCase adicionarItensServicoUseCase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    private final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    public OrdemServicoController(
            final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase,
            final ListOrdemServicoQuery listOrdemServicoQuery,
            final DetailOrdemServicoQuery detailOrdemServicoQuery,
            final AdicionarItensServicoUseCase adicionarItensServicoUseCase,
            final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase,
            final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase,
            final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase
    ) {
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.finalizarDiagnosticoUseCase = finalizarDiagnosticoUseCase;
        this.listOrdemServicoQuery = listOrdemServicoQuery;
        this.detailOrdemServicoQuery = detailOrdemServicoQuery;
        this.adicionarItensServicoUseCase = adicionarItensServicoUseCase;
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.reprovarOrdemServicoUseCase = reprovarOrdemServicoUseCase;
        this.finalizarOrdemServicoUseCase = finalizarOrdemServicoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ListOrdemServicoOutput>> list() {
        return ResponseEntity.ok(this.listOrdemServicoQuery.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailOrdemServicoOutput> detail(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.detailOrdemServicoQuery.execute(id));
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<AdicionarItensServicoResponse> adicionarItens(
            @PathVariable final UUID id,
            @RequestBody @Valid final AdicionarItensServicoRequest request
    ) {
        final var itens = request.itens().stream()
                .map(item -> toCommand(id, item))
                .toList();

        final var output = this.adicionarItensServicoUseCase.execute(AdicionarItensServicoCommand.with(
                id,
                itens
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AdicionarItensServicoResponse.from(output));
    }

    private AdicionarItemServicoCommand toCommand(
            final UUID ordemServicoId,
            final AdicionarItemServicoRequest item
    ) {
        return AdicionarItemServicoCommand.with(
                ordemServicoId,
                ItemServicoTipo.valueOf(item.tipo().trim().toUpperCase()),
                item.descricao(),
                item.pecaId(),
                item.quantidade(),
                item.valorUnitario()
        );
    }

    @PatchMapping("/{id}/diagnostico")
    public ResponseEntity<OrdemServicoStatusResponse> iniciarDiagnostico(@PathVariable final UUID id) {
        final var output = this.iniciarDiagnosticoUseCase.execute(IniciarDiagnosticoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PatchMapping("/{id}/diagnostico/finalizar")
    public ResponseEntity<OrdemServicoStatusResponse> finalizarDiagnostico(
            @PathVariable final UUID id,
            @RequestBody @Valid final FinalizarDiagnosticoRequest request
    ) {
        final var output = this.finalizarDiagnosticoUseCase.execute(FinalizarDiagnosticoCommand.with(
                id,
                request.tempoPrevistoExecucaoDias(),
                request.tempoPrevistoExecucaoHoras()
        ));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PatchMapping("/{id}/aprovacao/aprovar")
    public ResponseEntity<OrdemServicoStatusResponse> aprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.aprovarOrdemServico(id));
    }

    @PatchMapping("/{id}/aprovacao/reprovar")
    public ResponseEntity<OrdemServicoStatusResponse> reprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.reprovarOrdemServico(id));
    }

    @GetMapping("/{id}/aprovacao/aprovar")
    public ResponseEntity<OrdemServicoStatusResponse> aprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.aprovarOrdemServico(id));
    }

    @GetMapping("/{id}/aprovacao/reprovar")
    public ResponseEntity<OrdemServicoStatusResponse> reprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.reprovarOrdemServico(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoStatusResponse> finalizar(@PathVariable final UUID id) {
        final var output = this.finalizarOrdemServicoUseCase.execute(FinalizarOrdemServicoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<OrdemServicoStatusResponse> entregar(@PathVariable final UUID id) {
        final var output = this.entregarOrdemServicoUseCase.execute(EntregarOrdemServicoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    private OrdemServicoStatusResponse aprovarOrdemServico(final UUID id) {
        final var output = this.aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(id));

        return OrdemServicoStatusResponse.from(output);
    }

    private OrdemServicoStatusResponse reprovarOrdemServico(final UUID id) {
        final var output = this.reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(id));

        return OrdemServicoStatusResponse.from(output);
    }

}
