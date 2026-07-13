package com.autoservice.application.veiculo.query;

import java.util.UUID;

public interface GetVeiculoByIdQuery {

    VeiculoOutput buscarPorId(UUID id);
}
