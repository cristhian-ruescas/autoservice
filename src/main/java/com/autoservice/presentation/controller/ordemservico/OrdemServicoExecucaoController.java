package com.autoservice.presentation.controller.ordemservico;

import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoCommand;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoCommand;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoUseCase;
import com.autoservice.presentation.dto.ordemservico.OrdemServicoStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ordens-servico")
@Tag(name = "Ordens de serviço", description = "Finalização e entrega da ordem de serviço.")
public class OrdemServicoExecucaoController {

    private final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase;
    private final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase;

    public OrdemServicoExecucaoController(
            final FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase,
            final EntregarOrdemServicoUseCase entregarOrdemServicoUseCase
    ) {
        this.finalizarOrdemServicoUseCase = finalizarOrdemServicoUseCase;
        this.entregarOrdemServicoUseCase = entregarOrdemServicoUseCase;
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar execução da OS", description = "EM_EXECUCAO → FINALIZADA.")
    public ResponseEntity<OrdemServicoStatusResponse> finalizar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.finalizarOrdemServicoUseCase.execute(FinalizarOrdemServicoCommand.with(id))
        ));
    }

    @PatchMapping("/{id}/entregar")
    @Operation(summary = "Registrar entrega do veículo", description = "FINALIZADA ou REPROVADO → ENTREGUE.")
    public ResponseEntity<OrdemServicoStatusResponse> entregar(@PathVariable final UUID id) {
        return ResponseEntity.ok(OrdemServicoStatusResponse.from(
                this.entregarOrdemServicoUseCase.execute(EntregarOrdemServicoCommand.with(id))
        ));
    }
}
