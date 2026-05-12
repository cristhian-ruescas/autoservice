package com.autoservice.application.tipoveiculo.query;

import java.util.UUID;

public interface GetTipoVeiculoByIdQuery {

    TipoVeiculoOutput buscarPorId(UUID id);
}
