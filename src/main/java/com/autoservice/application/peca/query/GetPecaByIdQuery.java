package com.autoservice.application.peca.query;

import java.util.UUID;

public interface GetPecaByIdQuery {

    PecaOutput buscarPorId(UUID id);
}
