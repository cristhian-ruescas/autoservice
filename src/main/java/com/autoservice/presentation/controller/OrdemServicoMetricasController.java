package com.autoservice.presentation.controller;

import com.autoservice.application.ordemservico.metricas.TempoMedioExecucaoOutput;
import com.autoservice.application.ordemservico.metricas.TempoMedioExecucaoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordens-servico/metricas")
@Tag(name = "Ordem de serviço — métricas", description = "Indicadores administrativos sobre execução.")
public class OrdemServicoMetricasController {

    private final TempoMedioExecucaoQuery tempoMedioExecucaoQuery;

    public OrdemServicoMetricasController(final TempoMedioExecucaoQuery tempoMedioExecucaoQuery) {
        this.tempoMedioExecucaoQuery = tempoMedioExecucaoQuery;
    }

    @GetMapping("/tempo-medio-execucao")
    @Operation(
            summary = "Tempo médio de execução",
            description = """
                    Calcula a média do intervalo entre início da execução (`iniciadoEm`, ao aprovar o orçamento) \
                    e término (`finalizadoEm`, ao finalizar a OS), apenas para ordens em FINALIZADA ou ENTREGUE. \
                    O agrupamento por descrição usa os itens de linha do tipo SERVICO vinculados à OS."""
    )
    public ResponseEntity<TempoMedioExecucaoOutput> tempoMedioExecucao() {
        return ResponseEntity.ok(this.tempoMedioExecucaoQuery.consultar());
    }
}
