package com.autoservice.application.ordemservico.list;

import com.autoservice.application.PaginationOutput;

public interface ListOrdemServicoQuery {

    PaginationOutput<ListOrdemServicoOutput> execute(int page, int size, String status);
}
