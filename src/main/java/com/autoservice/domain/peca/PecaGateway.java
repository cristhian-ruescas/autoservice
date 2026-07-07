package com.autoservice.domain.peca;

import java.util.Optional;

public interface PecaGateway {

    Peca create(Peca peca);

    Optional<Peca> findById(PecaID id);

    void deleteById(PecaID id);
}
