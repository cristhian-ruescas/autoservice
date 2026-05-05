package com.autoservice.application.cliente.query;

import java.util.UUID;

public interface GetClienteByIdQuery {

    ClienteDetailOutput buscarPorId(UUID id);
}
