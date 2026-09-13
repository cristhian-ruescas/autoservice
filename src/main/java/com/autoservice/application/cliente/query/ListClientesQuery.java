package com.autoservice.application.cliente.query;

import com.autoservice.application.PaginationOutput;

public interface ListClientesQuery {

    PaginationOutput<ClienteOutput> listar(int page, int size, String tipoPessoa);
}
