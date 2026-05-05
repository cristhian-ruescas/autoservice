package com.autoservice.application.ordemservico.acompanhamento;

import java.util.UUID;

public interface AcompanharOrdemServicoQuery {

    AcompanharOrdemServicoOutput acompanhar(UUID ordemServicoId);
}
