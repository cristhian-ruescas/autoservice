package com.autoservice.domain.estoque;

import java.util.Optional;

public interface EstoqueGateway {

    Estoque create(Estoque estoque);

    Optional<Estoque> findById(EstoqueID id);
}
