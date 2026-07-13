package com.autoservice.application.ordemcompra.query;

import java.util.List;
import java.util.UUID;

public interface OrdemCompraQuery {

    List<OrdemCompraOutput> listar();

    OrdemCompraOutput detalhar(UUID id);
}
