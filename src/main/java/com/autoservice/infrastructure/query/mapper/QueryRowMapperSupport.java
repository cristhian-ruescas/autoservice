package com.autoservice.infrastructure.query.mapper;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import com.autoservice.infrastructure.persistence.entity.EstoqueJpaEntity;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaFisicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJuridicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.VeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import com.autoservice.infrastructure.persistence.mapper.EstoqueMapper;
import com.autoservice.infrastructure.persistence.mapper.OrdemServicoMapper;
import com.autoservice.infrastructure.persistence.mapper.PecaMapper;
import com.autoservice.infrastructure.persistence.mapper.PessoaMapper;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
import com.autoservice.infrastructure.persistence.mapper.VeiculoMapper;

public final class QueryRowMapperSupport {

    private QueryRowMapperSupport() {
    }

    public static Cliente toCliente(final Object value) {
        if (value instanceof ClienteJpaEntity jpaEntity) {
            return ClienteMapper.toDomain(jpaEntity);
        }
        if (value instanceof Cliente cliente) {
            return cliente;
        }

        throw new IllegalArgumentException("Tipo inesperado para cliente na linha de consulta");
    }

    public static PessoaFisica toPessoaFisica(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof PessoaFisicaJpaEntity jpaEntity) {
            return (PessoaFisica) PessoaMapper.toDomain(jpaEntity);
        }
        if (value instanceof PessoaFisica pessoaFisica) {
            return pessoaFisica;
        }

        throw new IllegalArgumentException("Tipo inesperado para pessoa física na linha de consulta");
    }

    public static PessoaJuridica toPessoaJuridica(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof PessoaJuridicaJpaEntity jpaEntity) {
            return (PessoaJuridica) PessoaMapper.toDomain(jpaEntity);
        }
        if (value instanceof PessoaJuridica pessoaJuridica) {
            return pessoaJuridica;
        }

        throw new IllegalArgumentException("Tipo inesperado para pessoa jurídica na linha de consulta");
    }

    public static Veiculo toVeiculo(final Object value) {
        if (value instanceof VeiculoJpaEntity jpaEntity) {
            return VeiculoMapper.toDomain(jpaEntity);
        }
        if (value instanceof Veiculo veiculo) {
            return veiculo;
        }

        throw new IllegalArgumentException("Tipo inesperado para veículo na linha de consulta");
    }

    public static TipoVeiculo toTipoVeiculo(final Object value) {
        if (value instanceof TipoVeiculoJpaEntity jpaEntity) {
            return TipoVeiculoMapper.toDomain(jpaEntity);
        }
        if (value instanceof TipoVeiculo tipoVeiculo) {
            return tipoVeiculo;
        }

        throw new IllegalArgumentException("Tipo inesperado para tipo de veículo na linha de consulta");
    }

    public static OrdemServico toOrdemServico(final Object value) {
        if (value instanceof OrdemServicoJpaEntity jpaEntity) {
            return OrdemServicoMapper.toDomain(jpaEntity);
        }
        if (value instanceof OrdemServico ordemServico) {
            return ordemServico;
        }

        throw new IllegalArgumentException("Tipo inesperado para ordem de serviço na linha de consulta");
    }

    public static Estoque toEstoque(final Object value) {
        if (value instanceof EstoqueJpaEntity jpaEntity) {
            return EstoqueMapper.toDomain(jpaEntity);
        }
        if (value instanceof Estoque estoque) {
            return estoque;
        }

        throw new IllegalArgumentException("Tipo inesperado para estoque na linha de consulta");
    }

    public static Peca toPeca(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof PecaJpaEntity jpaEntity) {
            return PecaMapper.toDomain(jpaEntity);
        }
        if (value instanceof Peca peca) {
            return peca;
        }

        throw new IllegalArgumentException("Tipo inesperado para peça na linha de consulta");
    }
}
