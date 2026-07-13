package com.autoservice.application.ordemservico.itemservico;

import java.util.List;
import java.util.UUID;

public interface ListItensServicoQuery {

    List<AdicionarItemServicoOutput> execute(UUID ordemServicoId);
}
