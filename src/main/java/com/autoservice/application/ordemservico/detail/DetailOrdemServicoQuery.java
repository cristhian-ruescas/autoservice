package com.autoservice.application.ordemservico.detail;

import java.util.UUID;

public interface DetailOrdemServicoQuery {

    DetailOrdemServicoOutput execute(UUID ordemServicoId);
}
