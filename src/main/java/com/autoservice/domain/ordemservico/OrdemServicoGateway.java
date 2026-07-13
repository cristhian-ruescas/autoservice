package com.autoservice.domain.ordemservico;

import java.util.Optional;

public interface OrdemServicoGateway {

    OrdemServico create(OrdemServico ordemServico);

    OrdemServico update(OrdemServico ordemServico);

    Optional<OrdemServico> findById(OrdemServicoID id);
}
