package com.autoservice.application.estoque.query;

import java.util.List;
import java.util.UUID;

public interface EstoqueQuery {

    List<EstoqueOutput> listar();

    EstoqueOutput detalhar(UUID id);
}
