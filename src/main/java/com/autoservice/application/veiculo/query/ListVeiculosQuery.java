package com.autoservice.application.veiculo.query;

import com.autoservice.application.PaginationOutput;

import java.util.UUID;

public interface ListVeiculosQuery {

    PaginationOutput<VeiculoOutput> listar(
            int page,
            int size,
            String marca,
            String modelo,
            Integer ano,
            UUID proprietarioId
    );
}
