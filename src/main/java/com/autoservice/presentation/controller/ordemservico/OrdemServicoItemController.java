package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.ordemservico.itemservico.*;
import com.autoservice.presentation.dto.ordemservico.AdicionarItemServicoResponse;
import com.autoservice.presentation.dto.ordemservico.AdicionarItensServicoRequest;
import com.autoservice.presentation.dto.ordemservico.AdicionarItensServicoResponse;
import com.autoservice.presentation.dto.ordemservico.AtualizarItemServicoRequest;
import com.autoservice.presentation.mapper.ItemServicoRequestMapper;
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
@Tag(name = "Ordens de serviço", description = "Itens de orçamento da ordem de serviço.")
public class OrdemServicoItemController {

    private final AdicionarItensServicoUseCase adicionarItensServicoUseCase;
    private final ListItensServicoQuery listItensServicoQuery;
    private final AtualizarItemServicoUseCase atualizarItemServicoUseCase;
    private final RemoverItemServicoUseCase removerItemServicoUseCase;

    public OrdemServicoItemController(
            final AdicionarItensServicoUseCase adicionarItensServicoUseCase,
            final ListItensServicoQuery listItensServicoQuery,
            final AtualizarItemServicoUseCase atualizarItemServicoUseCase,
            final RemoverItemServicoUseCase removerItemServicoUseCase
    ) {
        this.adicionarItensServicoUseCase = adicionarItensServicoUseCase;
        this.listItensServicoQuery = listItensServicoQuery;
        this.atualizarItemServicoUseCase = atualizarItemServicoUseCase;
        this.removerItemServicoUseCase = removerItemServicoUseCase;
    }

    @PostMapping("/{id}/itens")
    @Operation(
            summary = "Adicionar itens à OS",
            description = "Serviços e peças com quantidade e valor. Exige status EM_DIAGNOSTICO."
    )
    public ResponseEntity<AdicionarItensServicoResponse> adicionarItens(
            @PathVariable final UUID id,
            @RequestBody @Valid final AdicionarItensServicoRequest request
    ) {
        final var itens = request.itens().stream()
                .map(item -> ItemServicoRequestMapper.toCommand(id, item))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AdicionarItensServicoResponse.from(
                        this.adicionarItensServicoUseCase.execute(AdicionarItensServicoCommand.with(id, itens))
                ));
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<AdicionarItemServicoResponse>> listarItens(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.listItensServicoQuery.execute(id).stream()
                .map(AdicionarItemServicoResponse::from)
                .toList());
    }

    @PutMapping("/{id}/itens/{itemId}")
    public ResponseEntity<AdicionarItemServicoResponse> atualizarItem(
            @PathVariable final UUID id,
            @PathVariable final UUID itemId,
            @RequestBody @Valid final AtualizarItemServicoRequest request
    ) {
        return ResponseEntity.ok(AdicionarItemServicoResponse.from(
                this.atualizarItemServicoUseCase.execute(AtualizarItemServicoCommand.with(
                        id,
                        itemId,
                        ItemServicoRequestMapper.parseTipoOpcional(request.tipo()),
                        request.descricao(),
                        request.pecaId(),
                        request.quantidade(),
                        request.valorUnitario()
                ))
        ));
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(
            @PathVariable final UUID id,
            @PathVariable final UUID itemId
    ) {
        this.removerItemServicoUseCase.execute(RemoverItemServicoCommand.with(id, itemId));
        return ResponseEntity.noContent().build();
    }
}
