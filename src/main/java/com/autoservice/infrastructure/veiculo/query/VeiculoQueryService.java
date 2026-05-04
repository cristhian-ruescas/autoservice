package com.autoservice.infrastructure.veiculo.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.veiculo.query.GetVeiculoByIdQuery;
import com.autoservice.application.veiculo.query.GetVeiculoByPlacaQuery;
import com.autoservice.application.veiculo.query.ListVeiculosByClienteQuery;
import com.autoservice.application.veiculo.query.ListVeiculosQuery;
import com.autoservice.application.veiculo.query.VeiculoOutput;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Placa;
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
        validarPaginacao(page, size);
        final var marcaNormalizada = normalize(marca);
        final var modeloNormalizado = normalize(modelo);
        final var proprietario = proprietarioId == null ? null : PessoaID.from(proprietarioId);

        final var query = """
                select v, tipoVeiculo, pf, pj
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                left join PessoaFisica pf on pf.id = v.proprietarioId
                left join PessoaJuridica pj on pj.id = v.proprietarioId
                where (:marca is null or lower(tipoVeiculo.marca.value) like :marca)
                  and (:modelo is null or lower(tipoVeiculo.modelo.value) like :modelo)
                  and (:ano is null or tipoVeiculo.ano.value = :ano)
                  and (:proprietarioId is null or v.proprietarioId = :proprietarioId)
                order by tipoVeiculo.marca.value asc, tipoVeiculo.modelo.value asc, v.placa.value asc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setParameter("marca", marcaNormalizada)
                .setParameter("modelo", modeloNormalizado)
                .setParameter("ano", ano)
                .setParameter("proprietarioId", proprietario)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::mapToOutput)
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
        final var query = """
                select v, tipoVeiculo, pf, pj
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                left join PessoaFisica pf on pf.id = v.proprietarioId
                left join PessoaJuridica pj on pj.id = v.proprietarioId
                where v.placa = :placa
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("placa", placaNormalizada)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Veículo não encontrado para a placa informada"));
        }

        return this.mapToOutput(rows.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public VeiculoOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Veículo é obrigatório para consulta"));
        }

        final var query = """
                select v, tipoVeiculo, pf, pj
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                left join PessoaFisica pf on pf.id = v.proprietarioId
                left join PessoaJuridica pj on pj.id = v.proprietarioId
                where v.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", VeiculoID.from(id))
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Veículo não encontrado"));
        }

        return this.mapToOutput(rows.getFirst());
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

    private Cliente buscarCliente(final ClienteID clienteId) {
        final var query = """
                select c
                from Cliente c
                where c.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, Cliente.class)
                .setParameter("id", clienteId)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado"));
        }

        return rows.getFirst();
    }

    private List<VeiculoOutput> buscarVeiculosPorProprietario(final PessoaID proprietarioId) {
        final var query = """
                select v, tipoVeiculo, pf, pj
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                left join PessoaFisica pf on pf.id = v.proprietarioId
                left join PessoaJuridica pj on pj.id = v.proprietarioId
                where v.proprietarioId = :proprietarioId
                order by tipoVeiculo.marca.value asc, tipoVeiculo.modelo.value asc, v.placa.value asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("proprietarioId", proprietarioId)
                .getResultList()
                .stream()
                .map(this::mapToOutput)
                .toList();
    }

    private VeiculoOutput mapToOutput(final Object[] row) {
        final var veiculo = (Veiculo) row[0];
        final var tipoVeiculo = (TipoVeiculo) row[1];
        final var pessoaFisica = (PessoaFisica) row[2];
        final var pessoaJuridica = (PessoaJuridica) row[3];

        return new VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue(),
                mapProprietario(pessoaFisica, pessoaJuridica)
        );
    }

    private VeiculoOutput.ProprietarioOutput mapProprietario(
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica
    ) {
        if (pessoaJuridica != null) {
            return new VeiculoOutput.ProprietarioOutput(
                    "JURIDICA",
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    valueOf(pessoaJuridica.getEmail()),
                    valueOf(pessoaJuridica.getTelefone())
            );
        }
        if (pessoaFisica != null) {
            return new VeiculoOutput.ProprietarioOutput(
                    "FISICA",
                    pessoaFisica.getNome(),
                    pessoaFisica.getCpf().getValue(),
                    null,
                    null,
                    valueOf(pessoaFisica.getEmail()),
                    valueOf(pessoaFisica.getTelefone())
            );
        }

        return null;
    }

    private long totalVeiculos(
            final String marca,
            final String modelo,
            final Integer ano,
            final PessoaID proprietarioId
    ) {
        final var query = """
                select count(v)
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                where (:marca is null or lower(tipoVeiculo.marca.value) like :marca)
                  and (:modelo is null or lower(tipoVeiculo.modelo.value) like :modelo)
                  and (:ano is null or tipoVeiculo.ano.value = :ano)
                  and (:proprietarioId is null or v.proprietarioId = :proprietarioId)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("marca", marca)
                .setParameter("modelo", modelo)
                .setParameter("ano", ano)
                .setParameter("proprietarioId", proprietarioId)
                .getSingleResult();
    }

    private String normalize(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return "%" + value.trim().toLowerCase() + "%";
    }

    private void validarPaginacao(final int page, final int size) {
        if (page < 0) {
            throw DomainException.with(new Error("Página não deve ser menor que zero"));
        }
        if (size <= 0) {
            throw DomainException.with(new Error("Tamanho da página deve ser maior que zero"));
        }
        if (size > 100) {
            throw DomainException.with(new Error("Tamanho da página não deve ser maior que 100"));
        }
    }

    private String valueOf(final Object valueObject) {
        return valueObject == null ? null : valueObject.toString();
    }
}
