package com.autoservice.application.ordemservico.orcamento;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;

public interface OrcamentoEmailSender {

    void send(DetailOrdemServicoOutput ordemServico, byte[] pdf);
}
