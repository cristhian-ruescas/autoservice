package com.autoservice.domain.ordemcompra;

import java.util.List;

public interface ItemOrdemCompraGateway {

    ItemOrdemCompra create(ItemOrdemCompra item);

    List<ItemOrdemCompra> findByOrdemCompraId(OrdemCompraID ordemCompraId);
}
