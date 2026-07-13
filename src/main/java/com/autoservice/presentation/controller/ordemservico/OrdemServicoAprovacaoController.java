package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.presentation.dto.ordemservico.OrdemServicoStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(name = "Ordens de serviço", description = "Aprovação e reprovação de orçamento.")
public class OrdemServicoAprovacaoController {

    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;

    public OrdemServicoAprovacaoController(
            final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase
    ) {
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.reprovarOrdemServicoUseCase = reprovarOrdemServicoUseCase;
    }

    @PatchMapping("/{id}/aprovacao/aprovar")
    @Operation(summary = "Aprovar orçamento (PATCH)")
    public ResponseEntity<OrdemServicoStatusResponse> aprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(id))
        ));
    }

    @PatchMapping("/{id}/aprovacao/reprovar")
    @Operation(summary = "Reprovar orçamento (PATCH)")
    public ResponseEntity<OrdemServicoStatusResponse> reprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(id))
        ));
    }

    @GetMapping("/{id}/aprovacao/aprovar")
    @Operation(summary = "Aprovar orçamento por link (GET)", description = "Destinado a links em e-mail.")
    public ResponseEntity<OrdemServicoStatusResponse> aprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(id))
        ));
    }

    @GetMapping("/{id}/aprovacao/reprovar")
    @Operation(summary = "Reprovar orçamento por link (GET)", description = "Destinado a links em e-mail.")
    public ResponseEntity<OrdemServicoStatusResponse> reprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(id))
        ));
    }
}
