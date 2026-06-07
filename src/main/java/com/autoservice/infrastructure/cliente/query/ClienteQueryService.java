package com.autoservice.infrastructure.cliente.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.cliente.query.*;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import com.autoservice.infrastructure.query.mapper.QueryRowMapperSupport;
import com.autoservice.infrastructure.query.PaginacaoValidator;
import com.autoservice.infrastructure.query.QueryFilterNormalizer;
import com.autoservice.infrastructure.query.mapper.ClienteReadModelMapper;
import com.autoservice.infrastructure.query.mapper.VeiculoReadModelMapper;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteQueryService implements ListClientesQuery, GetClienteByCpfQuery, GetClienteByIdQuery {

    private static final String CLIENTE_PESSOA_QUERY = """
            select c, pf, pj, representante
            from ClienteJpaEntity c
            left join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
            left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
            left join PessoaFisicaJpaEntity representante on representante.id = pj.representanteLegalId
            """;

    private final EntityManager entityManager;

    public ClienteQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<ClienteOutput> listar(
            final int page,
            final int size,
            final String tipoPessoa
    ) {
        PaginacaoValidator.validar(page, size);
        final var tipoPessoaNormalizado = QueryFilterNormalizer.tipoPessoa(tipoPessoa);

        final var query = CLIENTE_PESSOA_QUERY + """
                where (:tipoPessoa is null
                    or (:tipoPessoa = 'FISICA' and pf is not null)
                    or (:tipoPessoa = 'JURIDICA' and pj is not null))
                order by c.dataCadastro.value desc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setParameter("tipoPessoa", tipoPessoaNormalizado)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(ClienteReadModelMapper::fromQueryRow)
                .toList();

        return PaginationOutput.from(items, page, size, totalClientes(tipoPessoaNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteOutput buscarPorCpf(final String cpf) {
        final var cpfNormalizado = CPF.from(cpf);
        final var query = """
                select c, pf, pj, representante
                from ClienteJpaEntity c
                join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
                left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
                left join PessoaFisicaJpaEntity representante on representante.id = pj.representanteLegalId
                where pf.cpf = :cpf
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("cpf", cpfNormalizado)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado para o CPF informado"));
        }

        return ClienteReadModelMapper.fromQueryRow(rows.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDetailOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Cliente é obrigatório para consulta"));
        }

        final var query = CLIENTE_PESSOA_QUERY + " where c.id = :id";

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", ClienteID.from(id))
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado"));
        }

        final var cliente = ClienteReadModelMapper.fromQueryRow(rows.getFirst());
        final var clienteDomain = QueryRowMapperSupport.toCliente(rows.getFirst()[0]);

        return ClienteDetailOutput.from(cliente, buscarVeiculos(clienteDomain));
    }

    private List<ClienteDetailOutput.VeiculoOutput> buscarVeiculos(final com.autoservice.domain.cliente.Cliente cliente) {
        final var query = """
                select v, tipoVeiculo
                from VeiculoJpaEntity v
                join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                where v.proprietarioId = :proprietarioId
                order by tipoVeiculo.marca.value asc, tipoVeiculo.modelo.value asc, v.placa.value asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("proprietarioId", cliente.getPessoaId())
                .getResultList()
                .stream()
                .map(VeiculoReadModelMapper::fromClienteVeiculoQueryRow)
                .toList();
    }

    private long totalClientes(final String tipoPessoa) {
        final var query = """
                select count(c)
                from ClienteJpaEntity c
                left join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
                left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
                where (:tipoPessoa is null
                    or (:tipoPessoa = 'FISICA' and pf is not null)
                    or (:tipoPessoa = 'JURIDICA' and pj is not null))
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("tipoPessoa", tipoPessoa)
                .getSingleResult();
    }
}
