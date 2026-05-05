package com.autoservice.infrastructure.cliente.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.cliente.query.ClienteDetailOutput;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.cliente.query.GetClienteByIdQuery;
import com.autoservice.application.cliente.query.GetClienteByCpfQuery;
import com.autoservice.application.cliente.query.ListClientesQuery;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteQueryService implements ListClientesQuery, GetClienteByCpfQuery, GetClienteByIdQuery {

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
        validarPaginacao(page, size);
        final var tipoPessoaNormalizado = normalizeTipoPessoa(tipoPessoa);

        final var query = """
                select c, pf, pj, representante
                from Cliente c
                left join PessoaFisica pf on pf.id = c.pessoaId
                left join PessoaJuridica pj on pj.id = c.pessoaId
                left join PessoaFisica representante on representante.id = pj.representanteLegalId
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
                .map(this::mapToOutput)
                .toList();

        return PaginationOutput.from(items, page, size, totalClientes(tipoPessoaNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteOutput buscarPorCpf(final String cpf) {
        final var cpfNormalizado = CPF.from(cpf);
        final var query = """
                select c, pf, pj, representante
                from Cliente c
                join PessoaFisica pf on pf.id = c.pessoaId
                left join PessoaJuridica pj on pj.id = c.pessoaId
                left join PessoaFisica representante on representante.id = pj.representanteLegalId
                where pf.cpf = :cpf
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("cpf", cpfNormalizado)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado para o CPF informado"));
        }

        return this.mapToOutput(rows.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDetailOutput buscarPorId(final UUID id) {
        if (id == null) {
            throw DomainException.with(new Error("Cliente é obrigatório para consulta"));
        }

        final var query = """
                select c, pf, pj, representante
                from Cliente c
                left join PessoaFisica pf on pf.id = c.pessoaId
                left join PessoaJuridica pj on pj.id = c.pessoaId
                left join PessoaFisica representante on representante.id = pj.representanteLegalId
                where c.id = :id
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", ClienteID.from(id))
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Cliente não encontrado"));
        }

        final var cliente = this.mapToOutput(rows.getFirst());
        final var clienteEntity = (Cliente) rows.getFirst()[0];

        return ClienteDetailOutput.from(cliente, buscarVeiculos(clienteEntity));
    }

    private List<ClienteDetailOutput.VeiculoOutput> buscarVeiculos(final Cliente cliente) {
        final var query = """
                select v, tipoVeiculo
                from Veiculo v
                join TipoVeiculo tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                where v.proprietarioId = :proprietarioId
                order by tipoVeiculo.marca.value asc, tipoVeiculo.modelo.value asc, v.placa.value asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("proprietarioId", cliente.getPessoaId())
                .getResultList()
                .stream()
                .map(this::mapVeiculo)
                .toList();
    }

    private ClienteDetailOutput.VeiculoOutput mapVeiculo(final Object[] row) {
        final var veiculo = (Veiculo) row[0];
        final var tipoVeiculo = (TipoVeiculo) row[1];

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

    private ClienteOutput mapToOutput(final Object[] row) {
        final var cliente = (Cliente) row[0];
        final var pessoaFisica = (PessoaFisica) row[1];
        final var pessoaJuridica = (PessoaJuridica) row[2];
        final var representante = (PessoaFisica) row[3];

        if (pessoaJuridica != null) {
            return new ClienteOutput(
                    cliente.getId().getValue(),
                    "JURIDICA",
                    cliente.getDataCadastro().getValue(),
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    valueOf(pessoaJuridica.getEmail()),
                    valueOf(pessoaJuridica.getTelefone()),
                    mapRepresentanteLegal(representante)
            );
        }

        if (pessoaFisica == null) {
            throw DomainException.with(new Error("Pessoa do cliente não encontrada"));
        }

        return new ClienteOutput(
                cliente.getId().getValue(),
                "FISICA",
                cliente.getDataCadastro().getValue(),
                pessoaFisica.getNome(),
                pessoaFisica.getCpf().getValue(),
                null,
                null,
                valueOf(pessoaFisica.getEmail()),
                valueOf(pessoaFisica.getTelefone()),
                null
        );
    }

    private long totalClientes(final String tipoPessoa) {
        final var query = """
                select count(c)
                from Cliente c
                left join PessoaFisica pf on pf.id = c.pessoaId
                left join PessoaJuridica pj on pj.id = c.pessoaId
                where (:tipoPessoa is null
                    or (:tipoPessoa = 'FISICA' and pf is not null)
                    or (:tipoPessoa = 'JURIDICA' and pj is not null))
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("tipoPessoa", tipoPessoa)
                .getSingleResult();
    }

    private String normalizeTipoPessoa(final String tipoPessoa) {
        if (tipoPessoa == null || tipoPessoa.isBlank()) {
            return null;
        }

        final var normalized = tipoPessoa.trim().toUpperCase();
        if (!normalized.equals("FISICA") && !normalized.equals("JURIDICA")) {
            throw DomainException.with(new Error("Tipo de pessoa deve ser FISICA ou JURIDICA"));
        }

        return normalized;
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

    private ClienteOutput.RepresentanteLegalOutput mapRepresentanteLegal(
            final PessoaFisica representante
    ) {
        if (representante == null) {
            return null;
        }

        return new ClienteOutput.RepresentanteLegalOutput(
                representante.getId().getValue(),
                representante.getNome(),
                representante.getCpf().getValue(),
                valueOf(representante.getEmail()),
                valueOf(representante.getTelefone())
        );
    }

    private String valueOf(final Object valueObject) {
        return valueObject == null ? null : valueObject.toString();
    }
}
