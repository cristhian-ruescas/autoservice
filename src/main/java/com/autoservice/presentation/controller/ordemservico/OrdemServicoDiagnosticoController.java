package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoCommand;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoUseCase;
import com.autoservice.presentation.dto.ordemservico.FinalizarDiagnosticoRequest;
import com.autoservice.presentation.dto.ordemservico.OrdemServicoStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(name = "Ordens de serviço", description = "Diagnóstico e geração de orçamento.")
public class OrdemServicoDiagnosticoController {

    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;

    public OrdemServicoDiagnosticoController(
            final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase,
            final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase
    ) {
        this.iniciarDiagnosticoUseCase = iniciarDiagnosticoUseCase;
        this.finalizarDiagnosticoUseCase = finalizarDiagnosticoUseCase;
    }

    @PatchMapping("/{id}/diagnostico")
    @Operation(summary = "Iniciar diagnóstico", description = "Transição RECEBIDO → EM_DIAGNOSTICO.")
    public ResponseEntity<OrdemServicoStatusResponse> iniciarDiagnostico(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.iniciarDiagnosticoUseCase.execute(IniciarDiagnosticoCommand.with(id))
        ));
    }

    @PatchMapping("/{id}/diagnostico/finalizar")
    @Operation(
            summary = "Finalizar diagnóstico / gerar orçamento",
            description = "Define previsão de execução e passa para AGUARDANDO_APROVACAO. "
                    + "Após o commit, envia e-mail ao cliente com PDF do orçamento e links de aprovação/reprovação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento gerado; OS aguardando aprovação"),
            @ApiResponse(responseCode = "422", description = "Status inválido ou valor do orçamento zerado")
    })
    public ResponseEntity<OrdemServicoStatusResponse> finalizarDiagnostico(
            @PathVariable final UUID id,
            @RequestBody @Valid final FinalizarDiagnosticoRequest request
    ) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.finalizarDiagnosticoUseCase.execute(FinalizarDiagnosticoCommand.with(
                        id,
                        request.tempoPrevistoExecucaoDias(),
                        request.tempoPrevistoExecucaoHoras()
                ))
        ));
    }
}
