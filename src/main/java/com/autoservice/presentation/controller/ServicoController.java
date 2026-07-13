package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.servico.create.CadastrarServicoCommand;
import com.autoservice.application.servico.create.CadastrarServicoUseCase;
import com.autoservice.application.servico.delete.RemoverServicoCommand;
import com.autoservice.application.servico.delete.RemoverServicoUseCase;
import com.autoservice.application.servico.query.GetServicoByIdQuery;
import com.autoservice.application.servico.query.ListServicosQuery;
import com.autoservice.application.servico.query.ServicoOutput;
import com.autoservice.application.servico.update.AtualizarServicoCommand;
import com.autoservice.application.servico.update.AtualizarServicoUseCase;
import com.autoservice.presentation.dto.servico.AtualizarServicoRequest;
import com.autoservice.presentation.dto.servico.CadastrarServicoRequest;
import com.autoservice.presentation.dto.servico.CadastrarServicoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Catálogo de serviços ofertados pela oficina (referência para orçamentos).")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarServicoUseCase;
    private final ListServicosQuery listServicosQuery;
    private final GetServicoByIdQuery getServicoByIdQuery;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final RemoverServicoUseCase removerServicoUseCase;

    public ServicoController(
            final CadastrarServicoUseCase cadastrarServicoUseCase,
            final ListServicosQuery listServicosQuery,
            final GetServicoByIdQuery getServicoByIdQuery,
            final AtualizarServicoUseCase atualizarServicoUseCase,
            final RemoverServicoUseCase removerServicoUseCase
    ) {
        this.cadastrarServicoUseCase = cadastrarServicoUseCase;
        this.listServicosQuery = listServicosQuery;
        this.getServicoByIdQuery = getServicoByIdQuery;
        this.atualizarServicoUseCase = atualizarServicoUseCase;
        this.removerServicoUseCase = removerServicoUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<ServicoOutput>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String nome
    ) {
        return ResponseEntity.ok(this.listServicosQuery.listar(page, size, nome));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoOutput> buscarPorId(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.getServicoByIdQuery.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody @Valid final AtualizarServicoRequest request
    ) {
        final var output = this.atualizarServicoUseCase.execute(AtualizarServicoCommand.with(
                id,
                request.nome(),
                request.descricao(),
                request.valorReferencia()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerServicoUseCase.execute(RemoverServicoCommand.with(id));

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Cadastrar serviço no catálogo")
    public ResponseEntity<CadastrarServicoResponse> create(
            @RequestBody @Valid final CadastrarServicoRequest request
    ) {
        final var output = this.cadastrarServicoUseCase.execute(CadastrarServicoCommand.with(
                request.nome(),
                request.descricao(),
                request.valorReferencia()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CadastrarServicoResponse.from(output));
    }
}
