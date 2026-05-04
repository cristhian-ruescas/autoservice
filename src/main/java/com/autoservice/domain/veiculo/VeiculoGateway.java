package com.autoservice.domain.veiculo;

import java.util.Optional;

public interface VeiculoGateway {

    Veiculo create(Veiculo veiculo);

    Veiculo update(Veiculo veiculo);

    Optional<Veiculo> findById(VeiculoID id);

    void deleteById(VeiculoID id);
}
