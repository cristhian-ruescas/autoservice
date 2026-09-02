package com.autoservice.application.peca.query;

import com.autoservice.application.PaginationOutput;

public interface ListPecasQuery {

    PaginationOutput<PecaOutput> listar(
            int page,
            int size,
            String marca,
            String codigo
    );
}
