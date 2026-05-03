package com.autoservice.domain.tipoveiculo;

import java.util.Optional;

public interface TipoVeiculoGateway {

    TipoVeiculo create(TipoVeiculo tipoVeiculo);

    Optional<TipoVeiculo> findById(TipoVeiculoID id);

    Optional<TipoVeiculo> findByMarcaModeloAno(String marca, String modelo, Integer ano);
}
