package com.autoservice.application.ordemservico.status;

import java.util.UUID;

public interface ConsultarStatusOrdemServicoQuery {

    ConsultarStatusOrdemServicoOutput consultar(UUID ordemServicoId);
}
