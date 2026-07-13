package com.autoservice.application.veiculo.query;

public interface GetVeiculoByPlacaQuery {

    VeiculoOutput buscarPorPlaca(String placa);
}
