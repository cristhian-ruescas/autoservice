package com.autoservice.infrastructure.query.mapper;

import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
public final class OrdemServicoReadModelMapper {

    private OrdemServicoReadModelMapper() {
    }

    public static ListOrdemServicoOutput fromQueryRow(final Object[] row) {
        return toListOutput(
                QueryRowMapperSupport.toOrdemServico(row[0]),
                QueryRowMapperSupport.toVeiculo(row[1]),
                QueryRowMapperSupport.toTipoVeiculo(row[2]),
                QueryRowMapperSupport.toCliente(row[3]),
                QueryRowMapperSupport.toPessoaFisica(row[4]),
                QueryRowMapperSupport.toPessoaJuridica(row[5]),
                QueryRowMapperSupport.toPessoaFisica(row[6])
        );
    }

    public static ListOrdemServicoOutput toListOutput(
            final OrdemServico ordemServico,
            final Veiculo veiculo,
            final TipoVeiculo tipoVeiculo,
            final Cliente cliente,
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica,
            final PessoaFisica representante
    ) {
        return new ListOrdemServicoOutput(
                ordemServico.getId().getValue(),
                ordemServico.getStatus().name(),
                ordemServico.getDataCriacao().getValue(),
                ordemServico.getRelato(),
                ordemServico.getTempoPrevistoExecucaoDias(),
                ordemServico.getTempoPrevistoExecucaoHoras(),
                ordemServico.getIniciadoEm(),
                ordemServico.getFinalizadoEm(),
                VeiculoReadModelMapper.toOrdemServicoListVeiculoOutput(veiculo, tipoVeiculo),
                ClienteReadModelMapper.toOrdemServicoListClienteOutput(cliente, pessoaFisica, pessoaJuridica, representante)
        );
    }
}
