package com.autoservice.infrastructure.veiculo.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.veiculo.query.*;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import com.autoservice.infrastructure.query.PaginacaoValidator;
import com.autoservice.infrastructure.query.QueryFilterNormalizer;
import com.autoservice.infrastructure.query.mapper.VeiculoReadModelMapper;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VeiculoQueryService implements
        ListVeiculosQuery,
        GetVeiculoByIdQuery,
        GetVeiculoByPlacaQuery,
        ListVeiculosByClienteQuery {

    private static final String VEICULO_QUERY = """
            select v, tipoVeiculo, pf, pj
            from VeiculoJpaEntity v
            join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
            left join PessoaFisicaJpaEntity pf on pf.id = v.proprietarioId
            left join PessoaJuridicaJpaEntity pj on pj.id = v.proprietarioId
            """;

    private final EntityManager entityManager;

    public VeiculoQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<VeiculoOutput> listar(
            final int page,
            final int size,
            final String marca,
            final String modelo,
            final Integer ano,
            final UUID proprietarioId
    ) {
        PaginacaoValidator.validar(page, size);
        final var marcaNormalizada = QueryFilterNormalizer.buscaParcial(marca);
        final var modeloNormalizado = QueryFilterNormalizer.buscaParcial(modelo);
        final var proprietario = proprietarioId == null ? null : PessoaID.from(proprietarioId).getValue();

        final var query = VEICULO_QUERY + """
                where (:marca is null or lower(tipoVeiculo.marca) like :marca)
                  and (:modelo is null or lower(tipoVeiculo.modelo) like :modelo)
                  and (:ano is null or tipoVeiculo.ano = :ano)
                  and (:proprietarioId is null or v.proprietarioId = :proprietarioId)
                order by tipoVeiculo.marca asc, tipoVeiculo.modelo asc, v.placa asc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setParameter("marca", marcaNormalizada)
                .setParameter("modelo", modeloNormalizado)
                .setParameter("ano", ano == null ? null : Ano.from(ano))
                .setParameter("proprietarioId", proprietario)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(VeiculoReadModelMapper::fromQueryRow)
                .toList();

        return PaginationOutput.from(
                items,
                page,
                size,
                totalVeiculos(marcaNormalizada, modeloNormalizado, ano, proprietario)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public VeiculoOutput buscarPorPlaca(final String placa) {
        final var placaNormalizada = Placa.from(placa);
        final var query = VEICULO_QUERY + " where v.placa = :placa";

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("placa", placaNormalizada)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Veículo não encontrado para a placa informada"));
        }

        return VeiculoReadModelMapper.fromQueryRow(rows.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public VeiculoOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Veículo é obrigatório para consulta"));
        }

        final var query = VEICULO_QUERY + " where v.id = :id";

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", VeiculoID.from(id).getValue())
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Veículo não encontrado"));
        }

        return VeiculoReadModelMapper.fromQueryRow(rows.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeiculoOutput> listarPorCliente(final UUID clienteId) {
        if (clienteId == null) {
            throw DomainException.with(new Error("Cliente é obrigatório para consulta de veículos"));
        }

        final var cliente = buscarCliente(ClienteID.from(clienteId));

        return buscarVeiculosPorProprietario(cliente.getPessoaId());
    }

    private com.autoservice.domain.cliente.Cliente buscarCliente(final ClienteID clienteId) {
        final var query = """
                select c
                from ClienteJpaEntity c
                where c.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, ClienteJpaEntity.class)
                .setParameter("id", clienteId.getValue())
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado"));
        }

        return ClienteMapper.toDomain(rows.getFirst());
    }

    private List<VeiculoOutput> buscarVeiculosPorProprietario(final PessoaID proprietarioId) {
        final var query = VEICULO_QUERY + """
                where v.proprietarioId = :proprietarioId
                order by tipoVeiculo.marca asc, tipoVeiculo.modelo asc, v.placa asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("proprietarioId", proprietarioId.getValue())
                .getResultList()
                .stream()
                .map(VeiculoReadModelMapper::fromQueryRow)
                .toList();
    }

    private long totalVeiculos(
            final String marca,
            final String modelo,
            final Integer ano,
            final String proprietarioId
    ) {
        final var query = """
                select count(v)
                from VeiculoJpaEntity v
                join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                where (:marca is null or lower(tipoVeiculo.marca) like :marca)
                  and (:modelo is null or lower(tipoVeiculo.modelo) like :modelo)
                  and (:ano is null or tipoVeiculo.ano = :ano)
                  and (:proprietarioId is null or v.proprietarioId = :proprietarioId)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("marca", marca)
                .setParameter("modelo", modelo)
                .setParameter("ano", ano == null ? null : Ano.from(ano))
                .setParameter("proprietarioId", proprietarioId)
                .getSingleResult();
    }
}
