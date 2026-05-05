package com.autoservice.application.tipoveiculo.query;

import com.autoservice.application.PaginationOutput;

public interface ListTipoVeiculoQuery {

    PaginationOutput<TipoVeiculoOutput> listar(
            int page,
            int size,
            String marca,
            String modelo,
            Integer ano
    );
}
