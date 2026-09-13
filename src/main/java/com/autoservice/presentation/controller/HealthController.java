package com.autoservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Operação", description = "Endpoints para verificação de saúde da aplicação.")
public class HealthController {

    public static final String SERVICE = "service";
    public static final String STATUS = "status";
    public static final String AUTOSERVICE = "autoservice";
    private final HealthEndpoint healthEndpoint;

    public HealthController(final HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    @Operation(summary = "Health check geral", description = "Retorna o status geral da aplicação para monitoramento e load balancers.")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                STATUS, healthEndpoint.health().getStatus().getCode(),
                SERVICE, AUTOSERVICE,
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe", description = "Indica se a aplicação está viva e não precisa ser reiniciada.")
    public ResponseEntity<Map<String, Object>> live() {
        return ResponseEntity.ok(Map.of(
                STATUS, "UP",
                SERVICE, AUTOSERVICE,
                "kind", "liveness"
        ));
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe", description = "Indica se a aplicação está pronta para receber tráfego.")
    public ResponseEntity<Map<String, Object>> ready() {
        return ResponseEntity.ok(Map.of(
                STATUS, healthEndpoint.health().getStatus().getCode(),
                SERVICE, AUTOSERVICE,
                "kind", "readiness"
        ));
    }
}
