package com.autoservice.application.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;

import java.util.Objects;

public class TipoVeiculoResolver {

    private final TipoVeiculoGateway tipoVeiculoGateway;

    public TipoVeiculoResolver(final TipoVeiculoGateway tipoVeiculoGateway) {
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    public TipoVeiculo obterOuCriar(final String marca, final String modelo, final int ano) {
        return obterOuCriarComIndicador(marca, modelo, ano).tipoVeiculo();
    }

    public Resultado obterOuCriarComIndicador(final String marca, final String modelo, final int ano) {
        final var marcaVo = Marca.from(marca);
        final var modeloVo = Modelo.from(modelo);
        final var anoVo = Ano.from(ano);

        return this.tipoVeiculoGateway.findByMarcaModeloAno(marcaVo.getValue(), modeloVo.getValue(), anoVo.getValue())
                .map(tipoVeiculo -> new Resultado(tipoVeiculo, false))
                .orElseGet(() -> new Resultado(this.tipoVeiculoGateway.create(TipoVeiculo.newTipoVeiculo(
                        marcaVo,
                        modeloVo,
                        anoVo
                )), true));
    }

    public record Resultado(
            TipoVeiculo tipoVeiculo,
            boolean criado
    ) {
    }
}
