package com.autoservice.infrastructure.observability;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrdemServicoStatusPhaseTracker {

    private static final Map<OrdemServicoStatus, String> FASES = Map.of(
            OrdemServicoStatus.EM_DIAGNOSTICO, "diagnostico",
            OrdemServicoStatus.EM_EXECUCAO, "execucao",
            OrdemServicoStatus.FINALIZADA, "finalizacao"
    );

    private final Map<String, Instant> inicioPorFase = new ConcurrentHashMap<>();

    public Optional<Duration> encerrarFase(final String ordemServicoId, final OrdemServicoStatus status) {
        final var fase = FASES.get(status);
        if (fase == null) {
            return Optional.empty();
        }

        final var key = chave(ordemServicoId, fase);
        final var inicio = inicioPorFase.remove(key);
        if (inicio == null) {
            return Optional.empty();
        }

        return Optional.of(Duration.between(inicio, Instant.now()));
    }

    public void iniciarFase(final String ordemServicoId, final OrdemServicoStatus status) {
        final var fase = FASES.get(status);
        if (fase == null) {
            return;
        }

        inicioPorFase.put(chave(ordemServicoId, fase), Instant.now());
    }

    public static String nomeFaseParaMetrica(final OrdemServicoStatus status) {
        final var fase = FASES.get(status);
        return fase != null ? fase : status.name().toLowerCase();
    }

    private static String chave(final String ordemServicoId, final String fase) {
        return ordemServicoId + ":" + fase;
    }
}
