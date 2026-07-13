package com.autoservice.application.ordemservico.orcamento;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;

public interface OrcamentoPdfGenerator {

    byte[] generate(DetailOrdemServicoOutput ordemServico);
}
