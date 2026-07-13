package com.autoservice.application.estoque.query;

import com.autoservice.application.PaginationOutput;

import java.util.UUID;

public interface EstoqueQuery {

    PaginationOutput<EstoqueOutput> listar(int page, int size);

    EstoqueOutput detalhar(UUID id);
}
