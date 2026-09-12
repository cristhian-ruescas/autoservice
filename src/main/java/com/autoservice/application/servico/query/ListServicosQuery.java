package com.autoservice.application.servico.query;

import com.autoservice.application.PaginationOutput;

public interface ListServicosQuery {

    PaginationOutput<ServicoOutput> listar(int page, int size, String nome);
}
