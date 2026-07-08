package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.cliente.create.CadastrarClienteCommand;
import com.autoservice.application.cliente.create.CadastrarClienteUseCase;
import com.autoservice.application.cliente.delete.RemoverClienteCommand;
import com.autoservice.application.cliente.delete.RemoverClienteUseCase;
import com.autoservice.application.cliente.query.*;
import com.autoservice.application.cliente.update.AtualizarClienteCommand;
import com.autoservice.application.cliente.update.AtualizarClienteUseCase;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.presentation.dto.cliente.AtualizarClienteRequest;
import com.autoservice.presentation.dto.cliente.CadastrarClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ListClientesQuery listClientesQuery;
    private final GetClienteByIdQuery getClienteByIdQuery;
    private final GetClienteByCpfQuery getClienteByCpfQuery;
    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final RemoverClienteUseCase removerClienteUseCase;

    public ClienteController(
            final ListClientesQuery listClientesQuery,
            final GetClienteByIdQuery getClienteByIdQuery,
            final GetClienteByCpfQuery getClienteByCpfQuery,
            final CadastrarClienteUseCase cadastrarClienteUseCase,
            final AtualizarClienteUseCase atualizarClienteUseCase,
            final RemoverClienteUseCase removerClienteUseCase
    ) {
        this.listClientesQuery = listClientesQuery;
        this.getClienteByIdQuery = getClienteByIdQuery;
        this.getClienteByCpfQuery = getClienteByCpfQuery;
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.removerClienteUseCase = removerClienteUseCase;
    }

    @GetMapping
    public ResponseEntity<PaginationOutput<ClienteOutput>> listar(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String tipoPessoa
    ) {
        return ResponseEntity.ok(this.listClientesQuery.listar(page, size, tipoPessoa));
    }

    @PostMapping
    public ResponseEntity<ClienteOutput> cadastrar(@RequestBody @Valid final CadastrarClienteRequest request) {
        final var output = this.cadastrarClienteUseCase.execute(CadastrarClienteCommand.with(
                TipoPessoaAtendimento.valueOf(request.tipoPessoa().trim().toUpperCase()),
                request.nome(),
                request.cpf(),
                request.razaoSocial(),
                request.cnpj(),
                request.representanteNome(),
                request.representanteCpf(),
                request.representanteEmail(),
                request.representanteTelefone(),
                request.email(),
                request.telefone()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteOutput> buscarPorCpf(@PathVariable final String cpf) {
        return ResponseEntity.ok(this.getClienteByCpfQuery.buscarPorCpf(cpf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDetailOutput> buscarPorId(@PathVariable final UUID id) {
        return ResponseEntity.ok(this.getClienteByIdQuery.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteOutput> atualizar(
            @PathVariable final UUID id,
            @RequestBody @Valid final AtualizarClienteRequest request
    ) {
        final var output = this.atualizarClienteUseCase.execute(AtualizarClienteCommand.with(
                id,
                request.nome(),
                request.razaoSocial(),
                request.email(),
                request.telefone(),
                request.representanteNome(),
                request.representanteCpf(),
                request.representanteEmail(),
                request.representanteTelefone()
        ));

        return ResponseEntity.ok(output);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable final UUID id) {
        this.removerClienteUseCase.execute(RemoverClienteCommand.with(id));

        return ResponseEntity.noContent().build();
    }
}
