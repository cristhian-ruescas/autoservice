package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoCommand;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.presentation.dto.ordemservico.OrdemServicoStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(name = "Ordens de serviço", description = "Aprovação e reprovação de orçamento (API admin e links de e-mail).")
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
    @Operation(
            summary = "Aprovar orçamento (PATCH — admin)",
            description = "AGUARDANDO_APROVACAO → EM_EXECUCAO. Pode gerar ordem(ns) de compra para peças sem estoque."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado"),
            @ApiResponse(responseCode = "422", description = "OS não está aguardando aprovação")
    })
    public ResponseEntity<OrdemServicoStatusResponse> aprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(id))
        ));
    }

    @PatchMapping("/{id}/aprovacao/reprovar")
    @Operation(
            summary = "Reprovar orçamento (PATCH — admin)",
            description = "AGUARDANDO_APROVACAO → REPROVADO."
    )
    public ResponseEntity<OrdemServicoStatusResponse> reprovar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(id))
        ));
    }

    @GetMapping("/{id}/aprovacao/aprovar")
    @Operation(
            summary = "Aprovar orçamento por link (GET — público)",
            description = "Mesmo efeito do PATCH; usado nos links do e-mail de orçamento. Não exige JWT."
    )
    public ResponseEntity<OrdemServicoStatusResponse> aprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.aprovarOrdemServicoUseCase.execute(AprovarOrdemServicoCommand.with(id))
        ));
    }

    @GetMapping("/{id}/aprovacao/reprovar")
    @Operation(
            summary = "Reprovar orçamento por link (GET — público)",
            description = "Mesmo efeito do PATCH; usado nos links do e-mail. Não exige JWT."
    )
    public ResponseEntity<OrdemServicoStatusResponse> reprovarPorLink(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.reprovarOrdemServicoUseCase.execute(ReprovarOrdemServicoCommand.with(id))
        ));
    }
}
