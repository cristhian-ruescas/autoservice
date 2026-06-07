package com.autoservice.infrastructure.query.mapper;

import com.autoservice.application.cliente.query.ClienteDetailOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.veiculo.query.VeiculoOutput;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;

public final class VeiculoReadModelMapper {

    private VeiculoReadModelMapper() {
    }

    public static VeiculoOutput fromQueryRow(final Object[] row) {
        return toVeiculoOutput(
                QueryRowMapperSupport.toVeiculo(row[0]),
                QueryRowMapperSupport.toTipoVeiculo(row[1]),
                QueryRowMapperSupport.toPessoaFisica(row[2]),
                QueryRowMapperSupport.toPessoaJuridica(row[3])
        );
    }

    public static VeiculoOutput toVeiculoOutput(
            final Veiculo veiculo,
            final TipoVeiculo tipoVeiculo,
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica
    ) {
        return new VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue(),
                PessoaReadModelMapper.toProprietarioOutput(pessoaFisica, pessoaJuridica)
        );
    }

    public static ListOrdemServicoOutput.VeiculoOutput toOrdemServicoListVeiculoOutput(
            final Veiculo veiculo,
            final TipoVeiculo tipoVeiculo
    ) {
        return new ListOrdemServicoOutput.VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue()
        );
    }

    public static ClienteDetailOutput.VeiculoOutput toClienteDetailVeiculoOutput(
            final Veiculo veiculo,
            final TipoVeiculo tipoVeiculo
    ) {
        return new ClienteDetailOutput.VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue()
        );
    }

    public static ClienteDetailOutput.VeiculoOutput fromClienteVeiculoQueryRow(final Object[] row) {
        return toClienteDetailVeiculoOutput(
                QueryRowMapperSupport.toVeiculo(row[0]),
                QueryRowMapperSupport.toTipoVeiculo(row[1])
        );
    }
}
