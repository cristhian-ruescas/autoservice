package com.autoservice.domain.peca;

import java.util.Optional;

public interface PecaGateway {

    Peca create(Peca peca);

    Peca update(Peca peca);

    Optional<Peca> findById(PecaID id);
}
