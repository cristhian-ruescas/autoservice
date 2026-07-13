package com.autoservice.domain.servico;

import java.util.Optional;

public interface ServicoGateway {

    Servico create(Servico servico);

    Servico update(Servico servico);

    Optional<Servico> findById(ServicoID id);

    void deleteById(ServicoID id);
}
