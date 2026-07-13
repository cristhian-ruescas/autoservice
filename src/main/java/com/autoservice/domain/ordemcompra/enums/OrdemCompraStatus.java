package com.autoservice.domain.ordemcompra.enums;

public enum OrdemCompraStatus {
    PENDENTE("Pendente"),
    REALIZADO("Realizado");

    private final String descricao;

    OrdemCompraStatus(final String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
