package com.autoservice.application.servico.query;

import java.util.UUID;

public interface GetServicoByIdQuery {

    ServicoOutput buscarPorId(UUID id);
}
