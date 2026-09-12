package com.autoservice.application.ordemservico.diagnostico;

import java.util.UUID;

public record IniciarDiagnosticoCommand(
        UUID ordemServicoId
) {
    public static IniciarDiagnosticoCommand with(final UUID ordemServicoId) {
        return new IniciarDiagnosticoCommand(ordemServicoId);
    }
}
