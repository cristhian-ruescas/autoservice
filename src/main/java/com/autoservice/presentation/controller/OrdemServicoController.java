package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoQuery;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoCommand;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoUseCase;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoCommand;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.itemservico.*;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoOutput;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoUseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.presentation.dto.ordemservico.*;
import com.autoservice.validation.Error;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(
        name = "Ordens de serviço",
        description = "Ciclo de vida da OS: listagem, detalhe, diagnóstico, itens/orçamento, aprovação (inclui links por e-mail), finalização e entrega."
)
public class OrdemServicoController {

    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;
    private final ListOrdemServicoQuery listOrdemServicoQuery;
    private final DetailOrdemServicoQuery detailOrdemServicoQuery;
    private final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery;
    private final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase;
    private final RemoverOrdemServicoUseCase removerOrdemServicoUseCase;
    private final AdicionarItensServicoUseCase adicionarItensServicoUseCase;
    private final ListItensServicoQuery listItensServicoQuery;
    private final AtualizarItemServicoUseCase atualizarItemServicoUseCase;
    private final RemoverItemServicoUseCase removerItemServicoUseCase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    private final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    public OrdemServicoController(
            final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase,
            final ListOrdemServicoQuery listOrdemServicoQuery,
            final DetailOrdemServicoQuery detailOrdemServicoQuery,
            final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery,
            final AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase,
            final RemoverOrdemServicoUseCase removerOrdemServicoUseCase,
            final AdicionarItensServicoUseCase adicionarItensServicoUseCase,
            final ListItensServicoQuery listItensServicoQuery,
            final AtualizarItemServicoUseCase atualizarItemServicoUseCase,
            final RemoverItemServicoUseCase removerItemServicoUseCase,
            final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase,
            final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase,
            final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase
    ) {
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.finalizarDiagnosticoUseCase = finalizarDiagnosticoUseCase;
        this.listOrdemServicoQuery = listOrdemServicoQuery;
        this.detailOrdemServicoQuery = detailOrdemServicoQuery;
        this.acompanharOrdemServicoQuery = acompanharOrdemServicoQuery;
        this.atualizarOrdemServicoUseCase = atualizarOrdemServicoUseCase;
        this.removerOrdemServicoUseCase = removerOrdemServicoUseCase;
        this.adicionarItensServicoUseCase = adicionarItensServicoUseCase;
        this.listItensServicoQuery = listItensServicoQuery;
        this.atualizarItemServicoUseCase = atualizarItemServicoUseCase;
        this.removerItemServicoUseCase = removerItemServicoUseCase;
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.reprovarOrdemServicoUseCase = reprovarOrdemServicoUseCase;
        this.finalizarOrdemServicoUseCase = finalizarOrdemServicoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
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
        final var output = this.atualizarOrdemServicoUseCase.execute(AtualizarOrdemServicoCommand.with(
                id,
                request.veiculoId(),
                request.relato(),
                request.tempoPrevistoExecucaoDias(),
                request.tempoPrevistoExecucaoHoras()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrdemServicoStatusResponse> remover(@PathVariable final UUID id) {
        final var output = this.removerOrdemServicoUseCase.execute(RemoverOrdemServicoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PostMapping("/{id}/itens")
    @Operation(
            summary = "Adicionar itens à OS",
            description = "Serviços e peças com quantidade e valor. Exige status EM_DIAGNOSTICO (etapa de orçamento no fluxo)."
    )
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

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<AdicionarItemServicoResponse>> listarItens(@PathVariable final UUID id) {
        final var itens = this.listItensServicoQuery.execute(id).stream()
                .map(AdicionarItemServicoResponse::from)
                .toList();

        return ResponseEntity.ok(itens);
    }

    private AdicionarItemServicoCommand toCommand(
            final UUID ordemServicoId,
            final AdicionarItemServicoRequest item
    ) {
        return AdicionarItemServicoCommand.with(
                ordemServicoId,
                parseItemServicoTipo(item.tipo()),
                item.descricao(),
                item.pecaId(),
                item.quantidade(),
                item.valorUnitario()
        );
    }

    @PutMapping("/{id}/itens/{itemId}")
    public ResponseEntity<AdicionarItemServicoResponse> atualizarItem(
            @PathVariable final UUID id,
            @PathVariable final UUID itemId,
            @RequestBody @Valid final AtualizarItemServicoRequest request
    ) {
        final var output = this.atualizarItemServicoUseCase.execute(AtualizarItemServicoCommand.with(
                id,
                itemId,
                request.tipo() == null || request.tipo().isBlank() ? null : parseItemServicoTipo(request.tipo()),
                request.descricao(),
                request.pecaId(),
                request.quantidade(),
                request.valorUnitario()
        ));

        return ResponseEntity.ok(AdicionarItemServicoResponse.from(output));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable final UUID id,
            @PathVariable final UUID itemId
    ) {
        this.removerItemServicoUseCase.execute(RemoverItemServicoCommand.with(id, itemId));

        return ResponseEntity.noContent().build();
    }

    private ItemServicoTipo parseItemServicoTipo(final String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw DomainException.with(new Error("Tipo do item de serviço não deve ser nulo"));
        }

        try {
            return ItemServicoTipo.valueOf(tipo.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("Tipo do item de serviço deve ser SERVICO ou PECA"));
        }
    }

    @PatchMapping("/{id}/diagnostico")
    @Operation(summary = "Iniciar diagnóstico", description = "Transição RECEBIDO → EM_DIAGNOSTICO.")
    public ResponseEntity<OrdemServicoStatusResponse> iniciarDiagnostico(@PathVariable final UUID id) {
        final var output = this.iniciarDiagnosticoUseCase.execute(IniciarDiagnosticoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PatchMapping("/{id}/diagnostico/finalizar")
    @Operation(
            summary = "Finalizar diagnóstico / gerar orçamento",
            description = "Define previsão de execução, passa para AGUARDANDO_APROVACAO e dispara fluxo de envio de orçamento."
    )
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
    @Operation(summary = "Aprovar orçamento (PATCH)")
    public ResponseEntity<OrdemServicoStatusResponse> aprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.aprovarOrdemServico(id));
    }

    @PatchMapping("/{id}/aprovacao/reprovar")
    @Operation(summary = "Reprovar orçamento (PATCH)")
    public ResponseEntity<OrdemServicoStatusResponse> reprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.reprovarOrdemServico(id));
    }

    @GetMapping("/{id}/aprovacao/aprovar")
    @Operation(summary = "Aprovar orçamento por link (GET)", description = "Destinado a links em e-mail (sem corpo).")
    public ResponseEntity<OrdemServicoStatusResponse> aprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.aprovarOrdemServico(id));
    }

    @GetMapping("/{id}/aprovacao/reprovar")
    @Operation(summary = "Reprovar orçamento por link (GET)", description = "Destinado a links em e-mail (sem corpo).")
    public ResponseEntity<OrdemServicoStatusResponse> reprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.reprovarOrdemServico(id));
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar execução da OS", description = "EM_EXECUCAO → FINALIZADA.")
    public ResponseEntity<OrdemServicoStatusResponse> finalizar(@PathVariable final UUID id) {
        final var output = this.finalizarOrdemServicoUseCase.execute(FinalizarOrdemServicoCommand.with(id));

        return ResponseEntity.ok(OrdemServicoStatusResponse.from(output));
    }

    @PatchMapping("/{id}/entregar")
    @Operation(summary = "Registrar entrega do veículo", description = "FINALIZADA ou REPROVADO → ENTREGUE.")
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
