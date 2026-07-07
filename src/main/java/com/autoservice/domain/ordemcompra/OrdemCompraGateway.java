package com.autoservice.domain.ordemcompra;

import java.util.Optional;

public interface OrdemCompraGateway {

    OrdemCompra create(OrdemCompra ordemCompra);

    Optional<OrdemCompra> findById(OrdemCompraID id);
}
