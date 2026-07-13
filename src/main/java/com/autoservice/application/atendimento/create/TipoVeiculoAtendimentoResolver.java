package com.autoservice.application.atendimento.create;

import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;

public class TipoVeiculoAtendimentoResolver {

    private final TipoVeiculoResolver tipoVeiculoResolver;

    public TipoVeiculoAtendimentoResolver(final TipoVeiculoResolver tipoVeiculoResolver) {
        this.tipoVeiculoResolver = tipoVeiculoResolver;
    }

    public TipoVeiculoResolver.Resultado obterOuCriar(final AbrirAtendimentoCommand command) {
        return this.tipoVeiculoResolver.obterOuCriarComIndicador(
                command.marca(),
                command.modelo(),
                command.ano()
        );
    }
}
