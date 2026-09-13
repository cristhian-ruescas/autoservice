package com.autoservice.domain.ordemservico.enums;

public enum OrdemServicoStatus {
    RECEBIDO("Recebido"),
    EM_DIAGNOSTICO("Em Diagnóstico"),
    AGUARDANDO_APROVACAO("Aguardando Aprovação"),
    REPROVADO("Reprovado"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADA("Finalizada"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    OrdemServicoStatus(final String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
