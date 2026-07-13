package com.autoservice.presentation.controller;

import com.autoservice.infrastructure.ordemservico.notificacao.NotificacaoSimuladaStore;
import com.autoservice.presentation.dto.integracao.NotificacaoSimuladaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integracoes/notificacoes")
@Tag(name = "Integrações externas", description = "Consulta de notificações simuladas enviadas ao cliente.")
public class NotificacaoSimuladaController {

    private final NotificacaoSimuladaStore notificacaoSimuladaStore;

    public NotificacaoSimuladaController(final NotificacaoSimuladaStore notificacaoSimuladaStore) {
        this.notificacaoSimuladaStore = notificacaoSimuladaStore;
    }

    @GetMapping("/simuladas")
    @Operation(
            summary = "Listar notificações simuladas",
            description = "Retorna as últimas notificações de mudança de status geradas para o cliente (útil em desenvolvimento)."
    )
    public ResponseEntity<List<NotificacaoSimuladaResponse>> listar(
            @RequestParam(defaultValue = "20") final int limite
    ) {
        final var notificacoes = this.notificacaoSimuladaStore.listar(limite).stream()
                .map(NotificacaoSimuladaResponse::from)
                .toList();

        return ResponseEntity.ok(notificacoes);
    }

    @DeleteMapping("/simuladas")
    @Operation(summary = "Limpar notificações simuladas")
    public ResponseEntity<Void> limpar() {
        this.notificacaoSimuladaStore.limpar();
        return ResponseEntity.noContent().build();
    }
}
