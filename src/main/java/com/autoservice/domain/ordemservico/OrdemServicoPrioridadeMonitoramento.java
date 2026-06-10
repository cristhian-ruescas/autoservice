package com.autoservice.domain.ordemservico;

import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;

import java.util.EnumSet;
import java.util.Map;

public final class OrdemServicoPrioridadeMonitoramento {

    private static final Map<OrdemServicoStatus, Integer> PRIORIDADES = Map.of(
            OrdemServicoStatus.EM_EXECUCAO, 1,
            OrdemServicoStatus.AGUARDANDO_APROVACAO, 2,
            OrdemServicoStatus.EM_DIAGNOSTICO, 3,
            OrdemServicoStatus.RECEBIDO, 4
    );

    private static final EnumSet<OrdemServicoStatus> EXCLUIDOS_DA_LISTAGEM = EnumSet.of(
            OrdemServicoStatus.FINALIZADA,
            OrdemServicoStatus.ENTREGUE
    );

    private OrdemServicoPrioridadeMonitoramento() {
    }

    public static boolean excluidoDaListagemOperacional(final OrdemServicoStatus status) {
        return EXCLUIDOS_DA_LISTAGEM.contains(status);
    }

    public static int prioridade(final OrdemServicoStatus status) {
        return PRIORIDADES.getOrDefault(status, 5);
    }
}
